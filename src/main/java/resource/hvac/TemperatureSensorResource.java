package resource.hvac;

import model.descriptors.hvac.TemperatureSensorDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TemperatureSensorResource extends GenericResource<TemperatureSensorDescriptor>{

    private static Logger logger = LoggerFactory.getLogger(TemperatureSensorResource.class);

    private static final double MIN_TEMPERATURE_VALUE = 25.0;

    private static final double MAX_TEMPERATURE_VALUE = 30.0;

    private static final double MIN_TEMPERATURE_VARIATION = 0.1;

    private static final double MAX_TEMPERATURE_VARIATION = 1.0;

    public static final long UPDATE_PERIOD = 5000;

    private static final long TASK_DELAY_TIME = 5000;

    private static final String RESOURCE_TYPE = "iot.sensor.temperature";

    TemperatureSensorDescriptor temperatureSensor;

    private Random random;

    private Timer timer = null;

    public TemperatureSensorResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    private void init(){
        temperatureSensor = new TemperatureSensorDescriptor();
        timer = new Timer();
        random = new Random(System.currentTimeMillis());
        startPeriodicTask();
    }

    private void startPeriodicTask(){
        try{
            //logger.info("Starting periodic Update Task with Period: {} ms", UPDATE_PERIOD);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    temperatureSensor.setTemperature(temperatureSensor.getTemperature() + (((random.nextDouble() * 2) - 1) * MAX_TEMPERATURE_VARIATION));
                    notifyUpdate(temperatureSensor);
                }
            }, TASK_DELAY_TIME, UPDATE_PERIOD);

        }catch (Exception e){
            logger.error("Error executing periodic resource value ! Msg: {}", e.getLocalizedMessage());
        }
    }


    public static void main(String[] args) {

        TemperatureSensorResource resource = new TemperatureSensorResource();

        resource.addDataListener(new ResourceDataListener<TemperatureSensorDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<TemperatureSensorDescriptor> resource, TemperatureSensorDescriptor updatedValue) {

                if(resource != null && updatedValue != null)
                    logger.info("Device: {} -> New Value Received: {}", resource.getId(), updatedValue);
                else
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
            }
        });

    }

}

