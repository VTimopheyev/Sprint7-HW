public class Main {


    public static void main(String[] args) {

        Test test = new Test();

        test.loadingFromFile();
        test.printAllIssues();
        test.checkHistory();


        /*test.createIssues();
        System.out.println("Check created issues:");
        test.printAllIssues();

        test.checkHistory();

        test.updateIssues();
        System.out.println("Check updated issues:");
        test.printAllIssues();

       test.checkDelete();
       System.out.println("Check deleted issues:");
       test.printAllIssues();*/


    }
}
