package com.startechnology.start_core.machine.vacuum_pump;

public interface IVacuumPump {

    int getTier();

    int getPumpRate();

    int getPumpCap();

    class Empty implements IVacuumPump {

        @Override
        public int getTier() {
            return 0;
        }

        @Override
        public int getPumpRate() {
            return 0;
        }

        @Override
        public int getPumpCap() {
            return 0;
        }
        
    }

}
