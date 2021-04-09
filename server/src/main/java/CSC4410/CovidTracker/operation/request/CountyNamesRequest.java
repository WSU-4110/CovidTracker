package CSC4410.CovidTracker.operation.request;

import CSC4410.CovidTracker.model.CountyLocation;
import CSC4410.CovidTracker.model.CountyName;
import CSC4410.CovidTracker.util.Csv;
import CSC4410.CovidTracker.util.Filesystem;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A request for full county names for all counties in the United States.
 */
public class CountyNamesRequest {
    private static final String DATA_FILE = "data/us_county_coordinates.csv";

    private ArrayList<CountyName> names;

    /**
     * @return The request results, or none if the request has not been
     * executed yet.
     */
    public Iterable<CountyName> getResults() {
        return names;
    }

    public CountyNamesRequest() {}

    /**
     * Execute the request. Call getResults() to retreieve the results of the
     * request.
     */
    public void execute() throws IOException {

        String data = Filesystem.readFile(DATA_FILE);
        Iterable<CSVRecord> parsed = Csv.parseCsv(data);

        names = new ArrayList<CountyName>();

        for (CSVRecord record : parsed) {

            int fips;
            String name, stateCode;

            try {
                fips = Integer.parseInt(record.get("GEOID"));
                name = record.get("NAMELSAD");
                stateCode = record.get("STUSAB");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            names.add(new CountyName(fips, name, stateCode));

        }

    }
}
