package app_utility;



public class DataBaseHelper {

    //private variables
    private int _id;

    private String _brand_name;
    private String _brand_id;
    private String _model_name;
    private String _model_id;

    private String _license_plate;

    private String _image_base64;

    private String _model_year;

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

    public DataBaseHelper(String _brand_name, String _brand_id, String _model_name, String _model_id, String _license_plate,
                          String _image_base64, String _model_year){
        this._brand_name = _brand_name;
        this._brand_id = _brand_id;
        this._model_name = _model_name;
        this._model_id = _model_id;
        this._license_plate = _license_plate;
        this._image_base64 = _image_base64;
        this._model_year = _model_year;
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

    public int get_vehicle_id(){
        return this._id;
    }

    // setting id
    public void set_vehicle_id(int id){
        this._id = id;
    }

    public String get_license_plate(){
        return this._license_plate;
    }

    public void set_license_plate(String license_plate){
        this._license_plate = license_plate;
    }

    public String get_image_base64(){
        return this._image_base64;
    }

    public void set_image_base64(String image_base64){
        this._image_base64 = image_base64;
    }

    public String get_model_year(){
        return this._image_base64;
    }

    public void set_model_year(String model_year){
        this._model_year = model_year;
    }
}
