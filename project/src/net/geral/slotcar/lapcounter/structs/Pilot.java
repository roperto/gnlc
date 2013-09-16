package net.geral.slotcar.lapcounter.structs;

import java.security.InvalidParameterException;

public class Pilot {
	public static final int		NICKNAME_MAX_LENGTH	= 15;
	public static final int		NAME_MAX_LENGTH		= 50;
	public static final String	DEFAULT_NICKNAME	= "New Pilot";
	public static final String	DEFAULT_NAME		= "New Pilot Name";
	private static final String	RESERVED_CHARS		= "[;\\*]";
	
	public static Pilot fromString(final String s) {
		final String[] ss = s.split(";");
		if (ss.length < 2) throw new InvalidParameterException("Invalid pilot string: " + s);
		
		final String nick = ss[0];
		final String name = ss[1];
		
		return new Pilot(name, nick);
	}
	
	public int		laneCache	= 0;
	
	private String	name;
	private String	nickname;
	
	public Pilot() {
		this(DEFAULT_NAME, DEFAULT_NICKNAME);
	}
	
	public Pilot(final String name, final String nickname) {
		setName(name);
		setNickname(nickname);
	}
	
	public String getName() {
		return name;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setName(String s) {
		s = s.replaceAll(RESERVED_CHARS, " ");
		if (s.length() > NAME_MAX_LENGTH) s.substring(0, NICKNAME_MAX_LENGTH);
		s = s.trim();
		if (s.length() == 0) s = "Invalid Name";
		name = s;
	}
	
	public void setNickname(String s) {
		s = s.replaceAll(RESERVED_CHARS, " ");
		if (s.length() > NICKNAME_MAX_LENGTH) s.substring(0, NICKNAME_MAX_LENGTH);
		s = s.trim();
		if (s.length() == 0) s = "Invalid Nickname";
		nickname = s;
	}
	
	@Override
	public String toString() {
		return String.format("%s;%s", nickname, name);
	}
}
