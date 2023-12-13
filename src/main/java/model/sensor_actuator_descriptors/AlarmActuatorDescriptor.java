package model.sensor_actuator_descriptors;

import java.util.Random;

/** Generic alarm actuator model.
 * Value represents ON/OFF state.
 */
public class AlarmActuatorDescriptor extends GenericDescriptor<Boolean> {

    public AlarmActuatorDescriptor(){
        super(new Random().nextBoolean());
    }
    public AlarmActuatorDescriptor(Boolean value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
