package app_utility;



public class DataBaseHelper {

    //private variables
    private int _id;

    private int brand_id;
    private int model_id;

    private String _brand_name;
    private String _brand_id;
    private String _model_name;
    private String _model_id;

    private String _license_plate;

    private String _image_base64;

    private String _model_year;

    private String _insurance_info;

    private int _insurance_id;

    private String _emission_info;

    private int _emission_id;

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

    public DataBaseHelper(int _id,String _brand_name, int _brand_id, String _model_name, int _model_id, String _license_plate,
                          String _image_base64, String _model_year){
        this._id = _id;
        this._brand_name = _brand_name;
        this.brand_id = _brand_id;
        this._model_name = _model_name;
        this.model_id = _model_id;
        this._license_plate = _license_plate;
        this._image_base64 = _image_base64;
        this._model_year = _model_year;
    }

    public DataBaseHelper(String _license_plate, String _image_base64, String _model_year){
        this._license_plate = _license_plate;
        this._image_base64 = _image_base64;
        this._model_year = _model_year;
    }

    public DataBaseHelper(String _insurance_info, int _insurance_id) {
        this._insurance_info = _insurance_info;
        this._insurance_id = _insurance_id;
    }

    public DataBaseHelper(String _emission_info, int _emission_id, Object object){
        this._emission_info = _emission_info;
        this._emission_id = _emission_id;
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
        return this._model_year;
    }

    public void set_model_year(String model_year){
        this._model_year = model_year;
    }

    public int get_brand_id_no(){
        return this.brand_id;
    }

    public void set_brand_id_no(int brand_id){
        this.brand_id = brand_id;
    }

    public int get_model_id_no(){
        return this.model_id;
    }

    public void set_model_id_no(int model_id){
        this.model_id = model_id;
    }

    public String get_insurance_info(){
        return this._insurance_info;
    }

    public void set_insurance_info(String insurance_info){
        this._insurance_info = insurance_info;
    }

    public int get_insurance_id(){
        return this._insurance_id;
    }

    public void set_insurance_id(int insurance_id){
        this._insurance_id = insurance_id;
    }

    public String get_emission_info(){
        return this._insurance_info;
    }

    public void set_emission_info(String emission_info){
        this._emission_info = emission_info;
    }

    public int get_emission_id(){
        return this._emission_id;
    }

    public void set_emission_id(int emission_id){
        this._emission_id = emission_id;
    }
}
