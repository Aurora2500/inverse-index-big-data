package es.ulpgc.bigdata.inverse_index;




public class Controller {
    public void startServers() {
        SparkWebService sparkWebService = new SparkWebService();
        sparkWebService.startServerById();
    }
}
