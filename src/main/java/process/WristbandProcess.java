package process;

import device.WristbandSmartObject;
import model.descriptors.wristband.PersonDataDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import resource.wristband.AlarmActuatorResource;
import resource.wristband.GPSSensorResource;
import resource.wristband.HealthcareSensorResource;
import resource.wristband.PersonDataResource;

import java.util.HashMap;

public class WristbandProcess {

    private static final Logger logger = LogManager.getLogger();

    private static int ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static int STOP_MINUTE = 1;

    public static void main(String[] args) {
        try{
            //String wristbandId = UUID.randomUUID().toString();
            String wristbandId = "wristband47"; //valore fisso, altrimenti comparirebbero troppi messaggi retained

            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP,
                    ProcessConfiguration.MQTT_BROKER_PORT), wristbandId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("MQTT client Connected ! Client id: {}",wristbandId);
            System.out.println("Connected");

            WristbandSmartObject wristbandSmartObject = new WristbandSmartObject();

            PersonDataDescriptor personDataDescriptor = new PersonDataDescriptor("FPPCVL", "Filippo", "Cavalieri", 21
                    , 5, wristbandId);
            wristbandSmartObject.init(wristbandId, mqttClient,new HashMap<>(){
                {
                    put("healthcare", new HealthcareSensorResource());
                    put("gps", new GPSSensorResource());
                    put("alarm", new AlarmActuatorResource());
                    put("personal_info", new PersonDataResource(personDataDescriptor));
                }
            });

            wristbandSmartObject.start();

            //Stop the wristband after STOP_MINUTE
            try{
                Thread.sleep(ONE_MINUTE_IN_MILLISECONDS * STOP_MINUTE);
                wristbandSmartObject.stop();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
