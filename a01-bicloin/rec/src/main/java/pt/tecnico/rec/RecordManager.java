package pt.tecnico.rec;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the records.
 * 
 *
 */
public class RecordManager {

	/**
	 * Map where the records are stored.
	 */
	private final Map<String, RecordData> records = new HashMap<String, RecordData>();
	
	/**
	 * Returns the value to which the specified key is mapped. If this record contains no mapping for the key, associates an empty string with the key.
	 * @param key	the key whose associated value is to be returned
	 * @return		the value to which the specified key is mapped
	 */
	public RecordData readRecord(String key) {
		synchronized (key.intern()) {
			RecordData data = records.get(key);
			if (data == null)
				records.put(key, data = new RecordData("", 0, ""));
			return data;
		}
	}

	/**
	 * Associates the specified value with the specified key in this map. If the record previously contained a mapping for the key, the old value is replaced.
	 * @param key	key with which the specified value is to be associated
	 * @param value	value to be associated with the specified key
	 */
	public void writeRecord(String key, RecordData data) {
		synchronized (key.intern()) {
			RecordData oldData = records.get(key);
			if (oldData == null || oldData.compareTo(data) < 0) {
				records.put(key, data);
			}
		}
	}
}
