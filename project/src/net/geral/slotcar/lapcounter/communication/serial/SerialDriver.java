package net.geral.slotcar.lapcounter.communication.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.TooManyListenersException;
import net.geral.slotcar.lapcounter.communication.Communication;
import net.geral.slotcar.lapcounter.communication.CommunicationDetector;
import net.geral.slotcar.lapcounter.communication.CommunicationDetectorListener;
import net.geral.slotcar.lapcounter.communication.serial.detector.SerialDetector;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.structs.RaceLightState;

public class SerialDriver extends Communication implements SerialPortEventListener {
	public static final int		BAUD_RATE				= 57600;
	public static final int		FIRMWARE_VERSION		= 12;

	private static final int	OPEN_TIMEOUT_MS			= 2000;
	private static final int	BOOT_STEP_DELAY_MS		= 500;
	private static final int	BOOT_STEP_COUNT			= 10;
	private static final long	CHECK_VERSION_TIMEOUT	= 1000;
	private static final int	READ_BUFFER_SIZE		= 1024;
	private static final int	WRITE_BUFFER_SIZE		= 512;

	private static int checkBytesNeeded(byte message) {
		switch (message) {
			case MessageCode.A2P_INFORM_ERROR:
			case MessageCode.A2P_INFORM_RESETED:
			case MessageCode.A2P_INFORM_VERSION:
				return 1;

			case MessageCode.A2P_INFORM_PINS: // 00011.--- + byte (low 0-7) + byte
												// (mid 8-15) + byte (high 16-19)
				return 1 + (3 * MessageCode.BYTE_BYTES);

			case MessageCode.A2P_INFORM_TIMING_INFO: // 00101.--- (param=inform
														// uptime if 0, LPS
														// otherwise) + long (info)
				return 1 + MessageCode.LONG_BYTES;

			case MessageCode.A2P_INFORM_LAP: // 00110.--- (param=sensor) + long
												// (block time) + long (lap time)
				return 1 + (2 * MessageCode.LONG_BYTES);

			default: // assume no extras but print warning
				System.err.println("Cannot check bytes needed for message: " + message + ". One byte assumed.");
				return 1;
		}
	}

	public static String event2string(int e) {
		switch (e) {
			case SerialPortEvent.BI:
				return "BI";
			case SerialPortEvent.CD:
				return "CD";
			case SerialPortEvent.CTS:
				return "CTS";
			case SerialPortEvent.DATA_AVAILABLE:
				return "DATA_AVAILABLE";
			case SerialPortEvent.DSR:
				return "DSR";
			case SerialPortEvent.FE:
				return "FE";
			case SerialPortEvent.OE:
				return "OE";
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				return "OUTPUT_BUFFER_EMPTY";
			case SerialPortEvent.PE:
				return "PE";
			case SerialPortEvent.RI:
				return "RI";
			default:
				return "?" + e + "?";
		}
	}

	private SerialPort		serialPort		= null;
	private InputStream		input			= null;
	private OutputStream	output			= null;
	private DriverState		state			= DriverState.CREATING;
	private DriverError		error			= DriverError.NONE;
	private long			timer			= 0L;
	private int				step			= 0;
	private final byte[]	readBuffer		= new byte[READ_BUFFER_SIZE];
	private int				readBufferStart	= 0;
	private int				readBufferEnd	= 0;
	private int				readBufferSize	= 0;

	private final byte[]	writeBuffer		= new byte[WRITE_BUFFER_SIZE];

	private int				writeBufferSize	= 0;

	public SerialDriver(Kernel k) {
		super(k);
		state = DriverState.CLOSED;
	}

	private boolean changeState(DriverError e) {
		error = e;
		return changeState(DriverState.ERROR_DETECTED);
	}

	public boolean changeState(DriverState newState) {
		return changeState(newState, 0);
	}

	public boolean changeState(DriverState newState, int newStep) {
		Logger.log("Driver State: " + state + "(" + step + ") => " + newState + " (" + newStep + ")");

		if (state == DriverState.TERMINATED) {
			Logger.log("Cannot change state to '" + state.toString() + "', driver already terminated.");
			return true;
		}

		state = newState;
		step = newStep;
		return false;
	}

	private boolean checkVersion() {
		timer = System.currentTimeMillis() + CHECK_VERSION_TIMEOUT;
		write(MessageCode.P2A_REQUEST_VERSION);
		return changeState(DriverState.CHECK_VERSION_RESPONSE, 1);
	}

	private boolean checkVersionResponse() throws IOException {
		// check until found a byte
		if (readBufferSize == 0) {
			if (System.currentTimeMillis() < timer) return true;
			else return changeState(DriverError.VERSION_TIMEOUT);
		}

		// read it
		byte read = read();

		// if step 1, check header
		if (step == 1) {
			if (read == MessageCode.A2P_INFORM_VERSION) {
				return nextStep();
			}
			else {
				Logger.log("Invalid version header response, expected (" + MessageCode.A2P_INFORM_VERSION
						+ "), received (" + read + ").");
				return changeState(DriverError.INVALID_RESPONSE);
			}
		}

		// if step 2, check version
		if (step == 2) {
			if (read != FIRMWARE_VERSION) {
				Logger.log("Expected firmware: " + FIRMWARE_VERSION + ", received: " + read);
				return changeState(DriverError.WRONG_FIRMWARE);
			}
			Logger.log("Port " + kernel.config.Port + " open, version " + FIRMWARE_VERSION + " ok.");
			// finish
			return changeState(DriverState.OPEN);
		}

		// should not happen
		return changeState(DriverError.UNKNOWN);
	}

	private boolean clearInputBuffer() {
		readBufferSize = readBufferStart = readBufferEnd = 0;
		return changeState(DriverState.CHECK_VERSION);
	}

	@Override
	public void close() {
		input = null;
		output = null;
		state = DriverState.CLOSED;
		if (serialPort == null) {
			Logger.log("Cannot close communication, not open.");
		}
		else {
			serialPort.close();
			serialPort = null;
			Logger.log("Communication closed.");
		}
	}

	private void consume(int bytes) {
		for (int i = 0; i < bytes; i++) {
			read();
		}
	}

	@Override
	public CommunicationDetector createDetector(Kernel k, CommunicationDetectorListener l) {
		return new SerialDetector(k, l);
	}

	private boolean errorDetected() {
		Logger.log("Error: " + error.toString() + " (" + error.title + ").");
		close();
		return false;
	}

	private void fireProperReadEvent(byte b) {
		byte msg = (byte) (b & MessageCode.MASK_MESSAGE);
		byte param = (byte) (b & MessageCode.MASK_PARAMETER);

		switch (msg) {
			case MessageCode.A2P_INFORM_ERROR:
				fireErrorMessageEvent("Error #" + param);
				return;

			case MessageCode.A2P_INFORM_VERSION:
				fireVersionMessageEvent("Firmware " + param);
				return;

			case MessageCode.A2P_INFORM_PINS: // 00011.--- + byte (low 0-7) + byte
												// (mid 8-15) + byte (high 16-19)
				consume(3 * MessageCode.BYTE_BYTES);
				return;

			case MessageCode.A2P_INFORM_TIMING_INFO: // 00101.--- (param=inform
														// uptime if 0, LPS
														// otherwise) + long (info)
				consume(MessageCode.LONG_BYTES);
				return;

			case MessageCode.A2P_INFORM_LAP: // 00110.--- (param=sensor) + long
												// (block time) + long (lap time)
				fireLapEvent(param, readLong() / 1e6, readLong() / 1e6);
				return;

			case MessageCode.A2P_INFORM_RESETED:
				// not used, no extra bytes
				return;

			default: // not used, but warn
				System.err.println("No event for message " + msg + " (param=" + param + ").");
				return;
		}
	}

	private boolean fireReadEvents() {
		// returns TRUE if is waiting more information
		synchronized (readBuffer) {
			if (readBufferSize == 0) return true;

			do {
				byte msg = (byte) (peek() & MessageCode.MASK_MESSAGE);
				int bytesNeeded = checkBytesNeeded(msg);
				if (bytesNeeded > readBufferSize) return true; // waiting the rest of the data

				fireProperReadEvent(read());
			} while (readBufferSize > 0);
		}

		// finished firing, not waiting more
		return false;
	}

	public DriverState getDriverState() {
		return state;
	}

	public DriverError getError() {
		return error;
	}

	private boolean loop() {
		try {
			switch (state) {
				case CLOSED:
					return true;
				case OPEN:
					return openLoop();
				case ERROR_DETECTED:
					return errorDetected();
				case OPEN_REQUESTED:
					return openRequested();
				case WAIT_FOR_BOOT:
					return waitForBoot();
				case CLEAR_INPUT_BUFFER:
					return clearInputBuffer();
				case CHECK_VERSION:
					return checkVersion();
				case CHECK_VERSION_RESPONSE:
					return checkVersionResponse();
				default:
					Logger.error("Invalid state: " + state.toString());
					terminate(false);
					return false;
			}
		}
		catch (IOException e) {
			return changeState(DriverError.IO);
		}
		catch (NoSuchPortException e) {
			return changeState(DriverError.NO_SUCH_PORT);
		}
		catch (PortInUseException e) {
			return changeState(DriverError.PORT_IN_USE);
		}
		catch (UnsupportedCommOperationException e) {
			return changeState(DriverError.UNSUPPORTED_COMM_OPERATION);
		}
		catch (Exception e) {
			Logger.log(e);
			return changeState(DriverError.UNKNOWN);
		}
	}

	public boolean nextStep() {
		step++;
		return false;
	}

	@Override
	public boolean open() {
		if (state == DriverState.OPEN) return true;
		if (state != DriverState.CLOSED) return false; // trying to open already

		Logger.log("Requesting OPEN ...");
		changeState(DriverState.OPEN_REQUESTED);
		return false;
	}

	private boolean openLoop() {
		// anything to do? if not, let it yield
		if ((writeBufferSize == 0) && (readBufferSize == 0)) return true;

		// write all buffer
		if (writeBufferSize > 0) outputWriteBuffer();

		// read while has something
		while (readBufferSize > 0) {
			// if returns true, it is waiting more bytes. In that case, yield
			if (fireReadEvents()) return true;
		}

		// done something, do not yield yet (maybe there is more to read
		// quickly)
		return false;
	}

	private boolean openRequested() throws NoSuchPortException, PortInUseException, IOException,
			UnsupportedCommOperationException, TooManyListenersException {
		// try to open
		CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(kernel.config.Port);
		serialPort = (SerialPort) id.open("GNLC Serial Driver", OPEN_TIMEOUT_MS);
		input = serialPort.getInputStream();
		output = serialPort.getOutputStream();
		serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		// notifies
		serialPort.addEventListener(this);
		serialPort.notifyOnBreakInterrupt(true);
		serialPort.notifyOnCarrierDetect(true);
		serialPort.notifyOnCTS(true);
		serialPort.notifyOnDataAvailable(true);
		serialPort.notifyOnDSR(true);
		serialPort.notifyOnFramingError(true);
		// serialPort.notifyOnOutputEmpty(true);
		serialPort.notifyOnOverrunError(true);
		serialPort.notifyOnParityError(true);
		serialPort.notifyOnRingIndicator(true);
		// success!
		return changeState(DriverState.WAIT_FOR_BOOT);
	}

	private void outputWriteBuffer() {
		// check any send buffer, multithread-safe
		synchronized (writeBuffer) {
			if (writeBufferSize > 0) {
				try {
					output.write(writeBuffer, 0, writeBufferSize);
					writeBufferSize = 0;
				}
				catch (IOException e) {
					changeState(DriverError.IO);
					return;
				}
			}
		}
	}

	private byte peek() {
		synchronized (readBuffer) {
			// check size
			if (readBufferSize == 0) throw new RuntimeException("Nothing to peek.");
			// return peek
			return readBuffer[readBufferStart];
		}
	}

	private byte read() {
		synchronized (readBuffer) {
			// check size
			if (readBufferSize == 0) throw new RuntimeException("Nothing to read.");
			// get value
			byte b = readBuffer[readBufferStart];
			// decrease size
			readBufferSize--;
			// increase (and loop) start
			if (++readBufferStart == READ_BUFFER_SIZE) readBufferStart = 0;
			// return it
			return b;
		}
	}

	private long readLong() {
		long l = 0;
		for (int i = 0; i < MessageCode.LONG_BYTES; i++) {
			byte b = read();
			l += ((b & (long) 0xFF) << 8 * i); // (b & 0xFF) to avoid negative
												// bytes. (long) to not result
												// as int
		}
		Logger.log("<LONG< " + Long.toString(l, 16) + " (" + l + ")");
		return l;
	}

	private void readToBuffer() {
		try {
			if (input.available() == 0) return;
			synchronized (readBuffer) {
				while (input.available() > 0) {
					// check buffer
					if (readBufferSize >= READ_BUFFER_SIZE) {
						changeState(DriverError.READ_BUFFER_OVERFLOW);
						return;
					}
					// set byte
					int r = input.read();
					if (r == -1) {
						changeState(DriverError.END_OF_COMMUNICATION);
						return;
					}
					readBuffer[readBufferEnd] = (byte) r;
					Logger.log(kernel.config.Port + "<< 0x" + Integer.toHexString(r).toUpperCase());
					// increase size
					readBufferSize++;
					// increase (and loop) offset
					if (++readBufferEnd == READ_BUFFER_SIZE) readBufferSize = 0;
				}
			}
		}
		catch (IOException e) {
			changeState(DriverError.IO);
		}
	}

	@Override
	public void run() {
		while (state != DriverState.REQUEST_TERMINATE) {
			if (loop()) Thread.yield();
		}
		state = DriverState.TERMINATED;
		Logger.log("Driver execution terminated!");
	}

	private void send(byte b) {
		// if ready to send, do not use buffer
		if (state == DriverState.OPEN) {
			write(b);
			return;
		}

		// buffer to send when connected
		Logger.log("DISCONNECTED, writing to buffer: " + Integer.toHexString(b).toUpperCase());
		synchronized (writeBuffer) {
			if (writeBufferSize >= WRITE_BUFFER_SIZE) Logger.error("Write Buffer Overflow");
			writeBuffer[writeBufferSize++] = b;
		}
		if (state == DriverState.CLOSED) open();
		return;
	}

	private void send(byte msg, byte param) {
		msg &= MessageCode.MASK_MESSAGE;
		param &= MessageCode.MASK_PARAMETER;
		send((byte) (msg | param));
	}

	@Override
	public void serialEvent(SerialPortEvent e) {
		switch (e.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				readToBuffer();
				return;
			default:
				Logger.log("SerialEvent [" + event2string(e.getEventType()) + "] OLD=" + e.getOldValue() + "  NEW="
						+ e.getNewValue());
				return;
		}
	}

	@Override
	public void setLanePower(int laneIndex, boolean yn) {
		send(yn ? MessageCode.P2A_REQUEST_RELAY_OFF : MessageCode.P2A_REQUEST_RELAY_ON, (byte) laneIndex);
	}

	@Override
	public void setLightState(RaceLightState state) {
		switch (state) {
			case Off:
				send(MessageCode.P2A_REQUEST_SET_LIGHTS, (byte) 0);
				break;
			case Ready:
				send(MessageCode.P2A_REQUEST_SET_LIGHTS, (byte) (kernel.config.InvertRaceLightsLogic ? 2 : 1));
				break;
			case Set:
				send(MessageCode.P2A_REQUEST_SET_LIGHTS, (byte) (kernel.config.InvertRaceLightsLogic ? 1 : 2));
				break;
			case Go:
				send(MessageCode.P2A_REQUEST_SET_LIGHTS, (byte) 3);
				break;
			default:
				Logger.log(new InvalidParameterException("Invalid light state: " + state.toString()));
				break;
		}

	}

	@Override
	public void setPowerPause(boolean yn) {
		send(yn ? MessageCode.P2A_REQUEST_RELAY_PAUSE__STOP : MessageCode.P2A_REQUEST_RELAY_PAUSE__RESUME);
	}

	@Override
	public void setTrackPower(boolean yn) {
		send(yn ? MessageCode.P2A_REQUEST_SET_ALL_RELAYS__OFF : MessageCode.P2A_REQUEST_SET_ALL_RELAYS__ON);
	}

	public void terminate(boolean wait) {
		Logger.log("Driver Terminate Requested!");
		if (state != DriverState.CLOSED) close();
		changeState(DriverState.REQUEST_TERMINATE);

		if (wait) {
			while (state != DriverState.TERMINATED) {
				Thread.yield();
			}
		}
	}

	private boolean waitForBoot() throws IOException {
		if ((step == 0) || (System.currentTimeMillis() > timer)) {
			// finished steps
			if (step > BOOT_STEP_COUNT) return changeState(DriverError.BOOT_TIMEOUT);

			timer = System.currentTimeMillis() + BOOT_STEP_DELAY_MS;
			write(MessageCode.P2A_REQUEST_RESET);
			return nextStep();
		}

		// found something?
		if (readBufferSize > 0) return changeState(DriverState.CLEAR_INPUT_BUFFER);

		// wait more
		return true;
	}

	private void write(byte msg) {
		try {
			Logger.log(kernel.config.Port + ">> 0x" + Integer.toHexString(msg).toUpperCase());
			output.write(msg);
			output.flush();
		}
		catch (IOException e) {
			changeState(DriverError.IO);
		}
	}
}
