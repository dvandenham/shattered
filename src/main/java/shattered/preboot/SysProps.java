package shattered.preboot;

final class SysProps {

	private static final boolean DEVELOPER_MODE = Boolean.getBoolean("shattered.developer");

	public static final boolean LOG_CLASS_EXISTENCE = SysProps.DEVELOPER_MODE || Boolean.getBoolean("shattered.preboot.log.existence");
	public static final boolean LOG_ANNOTATIONS     = SysProps.DEVELOPER_MODE || Boolean.getBoolean("shattered.preboot.log.annotations");

	public static final boolean DUMP_CLASSES        = Boolean.getBoolean("shattered.preboot.transform.dump");
	public static final boolean LOG_TRANSFORM       = SysProps.DEVELOPER_MODE || Boolean.getBoolean("shattered.preboot.log.transform");
	public static final boolean LOG_TRANSFORM_ERROR = SysProps.DEVELOPER_MODE || Boolean.getBoolean("shattered.preboot.log.transform.errors");
}