package vn.hp.jobhunter.util.error;

public class PermissionException extends RuntimeException{
    public PermissionException(String mess){
        super(mess);
    }
}
