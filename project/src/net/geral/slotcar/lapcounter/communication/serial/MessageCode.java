package net.geral.slotcar.lapcounter.communication.serial;

// @formatter:off

public class MessageCode {
	public static final int	BYTE_BYTES	= 1;	// arduino byte
	public static final int	INT_BYTES	= 2;	// arduino int
	public static final int	LONG_BYTES	= 4;	// arduino long
	
	// public static final byte -> public static final byte
	// = (byte)					-> = = (byte)
	// ; //						-> ; // 
	public static final byte MASK_MESSAGE                      = (byte)0xF8; // 11111.000
	public static final byte MASK_PARAMETER                    = (byte)0x07; // 00000.111
	public static final byte P2A_RESERVED_00                   = (byte)0x00; // 00000.---
	public static final byte A2P_INFORM_ERROR                  = (byte)0x00; // 00000.---
	public static final byte P2A_REQUEST_RESET                 = (byte)0x08; // 00001.---
	public static final byte A2P_INFORM_RESETED                = (byte)0x08; // 00001.---
	public static final byte P2A_REQUEST_VERSION               = (byte)0x10; // 00010.---
	public static final byte A2P_INFORM_VERSION                = (byte)0x10; // 00010.---
	public static final byte P2A_REQUEST_PINS                  = (byte)0x18; // 00011.---
	public static final byte A2P_INFORM_PINS                   = (byte)0x18; // 00011.--- + byte (low 0-7) + byte (mid 8-15) + byte (high 16-19)
	public static final byte P2A_SET_PIN_STATE                 = (byte)0x20; // 00100.--- (param=set to false if 0, true otherwise) + byte (pin to set)
	public static final byte A2P_RESERVED_20                   = (byte)0x20; // 00100.---
	public static final byte P2A_REQUEST_TIMING_INFO           = (byte)0x28; // 00100.---
	public static final byte A2P_INFORM_TIMING_INFO            = (byte)0x28; // 00101.--- (param=inform uptime if 0, LPS otherwise) + long (info)
	public static final byte A2P_INFORM_TIMING_INFO__UPTIME    = (byte)0x28; // 00101.000
	public static final byte A2P_INFORM_TIMING_INFO__LPS       = (byte)0x29; // 00101.001
	public static final byte P2A_RESERVED_30                   = (byte)0x30; // 00110.---
	public static final byte A2P_INFORM_LAP                    = (byte)0x30; // 00110.--- (param=sensor) + long (block time) + long (lap time)
	public static final byte P2A_REQUEST_SET_ALL_RELAYS        = (byte)0x38; // 00111.--- (param=set to false if 0, true otherwise)
	public static final byte P2A_REQUEST_SET_ALL_RELAYS__OFF   = (byte)0x38; // 00111.000
	public static final byte P2A_REQUEST_SET_ALL_RELAYS__ON    = (byte)0x39; // 00111.001
	public static final byte A2P_RESERVED_38                   = (byte)0x38; // 00111.---
	public static final byte P2A_REQUEST_RELAY_ON              = (byte)0x40; // 01000.--- (param=relay)
	public static final byte A2P_RESERVED_40                   = (byte)0x40; // 01000.000
	public static final byte P2A_REQUEST_RELAY_OFF             = (byte)0x48; // 01001.--- (param=relay)
	public static final byte A2P_RESERVED_48                   = (byte)0x48; // 01001.---
	public static final byte P2A_REQUEST_RELAY_PAUSE           = (byte)0x50; // 01010.--- (param=stop if 0, resume otherwise)
	public static final byte P2A_REQUEST_RELAY_PAUSE__STOP     = (byte)0x50; // 01010.000
	public static final byte P2A_REQUEST_RELAY_PAUSE__RESUME   = (byte)0x51; // 01010.001
	public static final byte A2P_RESERVED_50                   = (byte)0x50; // 01010.---
	public static final byte P2A_REQUEST_SET_LIGHTS            = (byte)0x58; // 01011.--- (param=0xx, xx=lights)
	public static final byte A2P_RESERVED_58                   = (byte)0x58; // 01011.---
}

// @formatter:on
