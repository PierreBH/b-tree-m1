package fr.miage.fsgbd;

public class TestString implements Executable<String> {
	@Override
	public boolean execute(String arg1, String arg2) {
		return false;
	}

	@Override
	public int compare(String type, String type1) {
		return 0;
	}
//	public boolean execute(String str1, String str2) {
//		return (str1.length() > str2.length());
//	}
}