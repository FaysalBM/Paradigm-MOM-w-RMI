public class EPublishMode {
    String modeP;
    public EPublishMode(String mode){
        modeP = mode;
    }
    public boolean isRoundRobin(){
        return modeP.equals("RR");
    }
    public boolean isBroadcast(){
        return modeP.equals("B");
    }
}
