package vn.hp.jobhunter.util.error;

public class IdInvalidException extends RuntimeException{
    public IdInvalidException(String mess){
        super(mess);
    }
}
