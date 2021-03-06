package CSC4410.CovidTracker.operation.request;

import CSC4410.CovidTracker.model.CountyCovidData;
import CSC4410.CovidTracker.util.Csv;
import CSC4410.CovidTracker.util.Http;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A request for Covid-19 data for all counties in the United States.
 */
public class CovidDataRequest {

    public static final String URL = "https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-counties-recent.csv";

    private ArrayList<CountyCovidData> results = null;

    /**
     * @return The results of the request, or null if the request hasn't run yet.
     */
    public Iterable<CountyCovidData> getResults() {
        return results;
    }

    public CovidDataRequest() {}

    /**
     * Execute the request. Call getResults() to retreieve the results of the
     * request.
     */
    public void execute() throws IOException, InterruptedException, ParseException {

        this.results = new ArrayList<>();

        // request covid data from external API
        String response = Http.get(URL);

        // parse covid data as CSV
        ArrayList<CSVRecord> parsed = Csv.parseCsv(response);

        // get latest date of data
        CSVRecord latest = parsed.get(parsed.size() - 1);
        String latestDateString = latest.get("date");
        var sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date latestDate = sdf.parse(latestDateString);

        // get day before last date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(latestDate);
        calendar.add(Calendar.DATE, -1);
        Date previousDate = calendar.getTime();
        String previousDateString = sdf.format(previousDate);

        var previousIndices = new HashMap<Integer, Integer>();

        for (int i = 0; i < parsed.size(); ++i) {
            CSVRecord record = parsed.get(i);


            if (record.get("date").equals(previousDateString)) {

                // remember the current index so we can come back to this date
                int fips;
                try {
                    fips = Integer.parseInt(record.get("fips"));
                } catch (Exception e) {
                    // skip record if we don't have a fips id
                    continue;
                }
                previousIndices.put(fips, i);

            } else if (record.get("date").equals(latestDateString)) {

                int cases = 0;
                int dailyCases = 0;
                int deaths = 0;

                int fips;

                try {
                    fips = Integer.parseInt(record.get("fips"));
                } catch (Exception e) {
                    // skip record if we don't have a fips id
                    continue;
                }

                try {

                    cases = Integer.parseInt(record.get("cases"));

                    // retrieve the previous date of data to calculate number of
                    // new cases in the last 24 hours.
                    int previousCasesIndex = previousIndices.get(fips);
                    int previousCases = Integer.parseInt(parsed.get(previousCasesIndex).get("cases"));
                    dailyCases = cases - previousCases;

                    deaths = Integer.parseInt(record.get("deaths"));
                } catch (Exception e) {
                    // ignore parse failures
                }

                results.add(new CountyCovidData(fips, cases, dailyCases, deaths));

            }
        }
    }
}
