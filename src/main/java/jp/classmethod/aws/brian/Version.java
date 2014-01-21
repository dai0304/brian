package jp.classmethod.aws.brian;


/**
 * Portnoy APIのバージョン番号を保持する。
 * 
 * @since 1.0
 * @version $Id: Version.java 6175 2012-06-08 02:20:08Z miyamoto $
 * @author daisuke
 */
public final class Version {
	
	/**
	 * ビルド番号を返す。
	 * 
	 * @return ビルド番号
	 * @since 1.0
	 */
	public static String getBuildNumberString() {
		return "[WORKING]"; // maven-injection-plugin による自動書き換え
	}
	
	/**
	 * バージョン番号を返す。
	 * 
	 * @return バージョン番号
	 * @since 1.0
	 */
	public static String getVersionString() {
		return "[WORKING]"; // maven-injection-plugin による自動書き換え
	}
	
	private Version() {
	}
}
