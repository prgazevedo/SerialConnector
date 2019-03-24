package RAK811.gui;

public interface DisplayMessage {



        /**
         * Must be called from FX application context.
         * @param message
         */
        public void displayLog(String message);

        /**
         * Can be called from any context.
         * @param message
         */
        public void displayLogLater(String message);

        /**
         * Must be called from FX application context.
         * @param message
         */
        public void displayFrame(String message);

        /**
         * Can be called from any context.
         * @param message
         */
        public void displayFrameLater(String message);

}

