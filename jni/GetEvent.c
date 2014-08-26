#include <jni.h>

#include <string.h>
#include <android/log.h>
#include <linux/types.h>

/* Unix */
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

static int fd;
static int buffer_size = 1024;

jint
Java_com_graylin_ge_MainActivity_openDevice( JNIEnv* env,
                                                  jobject thiz,
                                                  jstring path)
{
	char *dev_path = (*env)->GetStringUTFChars(env, path, NULL);

	fd = open(dev_path, O_RDONLY);
//	fd = open(dev_path, O_RDONLY | O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
	if (fd < 0) {
//		printf("errno = %d\n", errno);
//		perror("Unable to open device");
		__android_log_print(ANDROID_LOG_ERROR, "gray", "%s", "Unable to open device");
		return -1;
	}

	return 0;
}

jint
Java_com_graylin_ge_MainActivity_closeDevice( JNIEnv* env,
                                                  jobject thiz )
{
	int res = close(fd);

	__android_log_print(ANDROID_LOG_ERROR, "gray", "closeDevice, res:%d", res);
	if (res != 0) {
		return -1;
	}
	return 0;
}

jstring
Java_com_graylin_ge_MainActivity_readDevice( JNIEnv* env,
                                                  jobject thiz )
{
	int res, index;
	char buf[buffer_size];
	char temp_string[buffer_size];
	char output_string[buffer_size];

	memset(buf, 0x0, sizeof(buf));
	memset(temp_string, 0x0, sizeof(buf));
	memset(output_string, 0x0, sizeof(buf));

	res = read(fd, buf, sizeof(buf));
	if (res < 0 ) {
		__android_log_print(ANDROID_LOG_ERROR, "gray", "read device error!");
	} else {
//		printf("\n=============================read %d bytes at %s \n", res, dev_path);
		sprintf(output_string, "read %d bytes\n", res);
		for (index = 0; index < res; ++index) {
//			printf("0x%02x ", buf[index]);
			sprintf(temp_string, "%02X ", buf[index]);
			strcat(output_string, temp_string);
		}
	}

//	string ssss(output_string);
//	__android_log_print(ANDROID_LOG_ERROR, "gray", "%s", output_string);

	return (*env)->NewStringUTF(env, output_string);
}

jstring
Java_com_graylin_ge_MainActivity_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{

	__android_log_print(ANDROID_LOG_ERROR, "gray", "Java_com_example_jnitest_MainActivity_stringFromJNI"); //Or ANDROID_LOG_INFO, ...

	int fd, res, index;
	char buf[512];
	char temp_string[512];
	char output_string[512];

	char dev_path[256] = "/dev/input/event6";
	// cant use hidrawx
//	char dev_path[256] = "/dev/hidraw0";
	fd = open(dev_path, O_RDONLY);
//	fd = open(dev_path, O_RDONLY);
//	fd = open("/dev/input/event3", O_RDWR | O_NONBLOCK);
//	fd = open("/dev/hidraw0", O_RDONLY); // NOK
//	fd = open("/dev/hidraw0", O_RDWR | O_NONBLOCK); // NOK

//	sprintf("%s\n", path);
//	sprintf(output_string, "%s\n", path);

	if (fd < 0) {
//		printf("errno = %d\n", errno);
//		perror("Unable to open device");
		__android_log_print(ANDROID_LOG_ERROR, "gray", "%s", "Unable to open device");
	}

	while(1){

	memset(buf, 0x0, sizeof(buf));
	memset(temp_string, 0x0, sizeof(buf));
	memset(output_string, 0x0, sizeof(buf));

	res = read(fd, buf, sizeof(buf));
	if (res < 0 ) {
//		printf("Error: %d, res:%d\n", errno, res);
//		perror("read");
//		close(fd);
//		return -11;
	} else {
//		printf("\n=============================read %d bytes at %s \n", res, dev_path);
//		sprintf(output_string, "open:%d, read:%d\n", fd, res);
		for (index = 0; index < res; ++index) {
//			printf("0x%02x ", buf[index]);
			sprintf(temp_string, "0x%02x ", buf[index]);
			strcat(output_string, temp_string);
		}
	}

//	string ssss(output_string);
	__android_log_print(ANDROID_LOG_ERROR, "gray", "%s", output_string);

	}

	close(fd);
//	return output_string;

    return (*env)->NewStringUTF(env, output_string);
}
