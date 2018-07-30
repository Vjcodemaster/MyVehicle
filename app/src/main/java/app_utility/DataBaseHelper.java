package app_utility;



public class DataBaseHelper {

    //private variables
    private int _id;

    private String _brand_name;
    private String _brand_id;
    private String _model_name;
    private String _model_id;

    // Empty constructor
    public DataBaseHelper(){

    }

    // constructor
    public DataBaseHelper(String _brand_name, String _brand_id, String _model_name, String _model_id){
        this._brand_name = _brand_name;
        this._brand_id = _brand_id;
        this._model_name = _model_name;
        this._model_id = _model_id;
    }

    public DataBaseHelper(String _model_name, String _model_id){
        //this._brand_name = _brand_name;
        //this._brand_id = _brand_id;
        this._model_name = _model_name;
        this._model_id = _model_id;
    }

    // getting brandname
    public String get_brand_name(){
        return this._brand_name;
    }

    public void set_brand_name(String brand_name){
        this._brand_name = brand_name;
    }

    public String get_brand_id(){
        return this._brand_id;
    }

    public void set_brand_id(String brand_id){
        this._brand_id = brand_id;
    }

    public String get_model_name(){
        return this._model_name;
    }

    public void set_model_name(String model_name){
        this._model_name = model_name;
    }

    public String get_model_id(){
        return this._model_id;
    }

    public void set_model_id(String model_id){
        this._model_id = model_id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

}
