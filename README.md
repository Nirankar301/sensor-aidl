# sensor-aidl

- Retreives phone orientation data using AIDL
- It uses Sensor TYPE_ROTATION_VECTOR and sensor data get displayed inside a TextView
- Sampling rate of sensor data is 8 milli seconds.
- Service for sensor is inside library module.
- Client bind with the service and retrieves the sensor data.
