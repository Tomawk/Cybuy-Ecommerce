package lsmd.group17.cybuy.gui.user.seller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.text.Text;
import lsmd.group17.cybuy.gui.general.SearchbarController;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.gui.prototypes.Controller;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.Analytics;
import lsmd.group17.cybuy.model.DailyAnalytics;
import lsmd.group17.cybuy.model.Product;
import lsmd.group17.cybuy.model.User;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsController extends Controller {

    @FXML protected Parent navbar;
    @FXML protected SearchbarController navbarController;

    @FXML protected ProgressBar progress1, progress2, progress3, progress4, progress5;
    @FXML protected Text number1, number2, number3, number4, number5;

    @FXML protected Text bestProduct, productSold, totalEarnings, averageTime, averageReview;

    @FXML protected LineChart<Number, Number> history;

    @FXML protected MenuButton target;
    @FXML protected RadioMenuItem maleItem, femaleItem, oldItem, youngItem, allItem;

    private XYChart.Series<Number, Number> all_sales, male_sales, female_sales, young_sales, old_sales;
    @Override
    protected void initialize() {

    }

    @Override
    public void setLink(Session s) {
        navbarController.setLink(s);

        User user = s.getUserLogged();

        if(user == null || user.getRole().equals("User")){
            navbarController.logMessage("Only a seller or an admin should see this page", SearchbarController.message_type.ERROR);
        }else{

            //Retrieve the analytics from the collection
            EventHandler handler = EventHandler.getInstance();
            Analytics analytics = handler.retrieveAnalytics(user) ;
            Product best = analytics.getBestSellingProduct();
            String description;

            description = (best == null) ? "-" : best.getDescription();

            bestProduct.setText(description);
            productSold.setText(String.valueOf(analytics.getTotalSales()));
            totalEarnings.setText("$" + String.format("%.2f", analytics.getTotalEarnings()));
            averageTime.setText(analytics.getAvgDelivery() + " day/s");

            DecimalFormat df = new DecimalFormat("###.##");
            averageReview.setText(df.format(analytics.getAvgReviews()));

            int[] stars = analytics.getStarsDistribution();
            double[] normalizedStars = new double[5];
            int max = 0, sum = 0;

            for(int i = 0; i < 5; i++) {
                max = Math.max(max, stars[i]);
                sum += stars[i];
            }

            for(int i = 0; i < 5; i++)
                normalizedStars[i] = 1.0 * stars[i] / sum;

            progress1.setProgress(normalizedStars[0]);
            progress2.setProgress(normalizedStars[1]);
            progress3.setProgress(normalizedStars[2]);
            progress4.setProgress(normalizedStars[3]);
            progress5.setProgress(normalizedStars[4]);

            number1.setText(String.valueOf(stars[0]));
            number2.setText(String.valueOf(stars[1]));
            number3.setText(String.valueOf(stars[2]));
            number4.setText(String.valueOf(stars[3]));
            number5.setText(String.valueOf(stars[4]));

            int length = LocalDate.now().lengthOfMonth();
            ArrayList<DailyAnalytics> daily = analytics.getSalesByDay();

            all_sales = new XYChart.Series<>();
            all_sales.setName("All Sales");

            male_sales = new XYChart.Series<>();
            male_sales.setName("Male Sales");

            female_sales = new XYChart.Series<>();
            female_sales.setName("Female Sales");

            young_sales = new XYChart.Series<>();
            young_sales.setName("Young Sales");

            old_sales = new XYChart.Series<>();
            old_sales.setName("Old Sales");

            for(int i = 0; i<length; i++) {
                DailyAnalytics singleDay = daily.get(i);
                all_sales.getData().add(new XYChart.Data<>(singleDay.getDay_num(), singleDay.getAll_sales()));
                male_sales.getData().add(new XYChart.Data<>(singleDay.getDay_num(), singleDay.getMale_sales()));
                female_sales.getData().add(new XYChart.Data<>(singleDay.getDay_num(), singleDay.getFemale_sales()));
                young_sales.getData().add(new XYChart.Data<>(singleDay.getDay_num(), singleDay.getYoung_sales()));
                old_sales.getData().add(new XYChart.Data<>(singleDay.getDay_num(), singleDay.getOld_sales()));
            }
        }
    }

    @FXML private void setGraph(){
        String name = "";
        ArrayList<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();

        if(maleItem.isSelected()){
            name += "males, ";
            seriesList.add(male_sales);
        }
        if(femaleItem.isSelected()){
            name += "females, ";
            seriesList.add(female_sales);
        }
        if(youngItem.isSelected()){
            name += "young, ";
            seriesList.add(young_sales);
        }
        if(oldItem.isSelected()){
            name += "olds, ";
            seriesList.add(old_sales);
        }
        if(allItem.isSelected()){
            name += "all, ";
            seriesList.add(all_sales);
        }

        history.getData().clear();
        history.getData().addAll(seriesList);

        name = name.equals("") ? "null" : name;
        target.setText(name);
    }

}
