package play.modules.rabbitmq.util;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.map.ObjectMapper;

// TODO: Auto-generated Javadoc
/**
 * The Class JSONMapper.
 */
public abstract class JSONMapper {
	
	/** The mapper. */
	private static transient ObjectMapper mapper;

	/**
	* Gets the mapper.
	*
	* @return the mapper
	*/
	public static ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}
	
	/**
	 * Gets the bytes.
	 *
	 * @param object the object
	 * @return the bytes
	 * @throws Exception the exception
	 */
	public static byte[] getBytes(Object object) throws Exception {
		String value = getMapper().writeValueAsString(object);
		return value.getBytes();
	}
	
	/**
	 * Gets the object.
	 *
	 * @param object the object
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object getObject(Class clazz, byte[] object) throws Exception {
		Object data = getMapper().readValue(new String(object), clazz);
		return data;
	}

}
