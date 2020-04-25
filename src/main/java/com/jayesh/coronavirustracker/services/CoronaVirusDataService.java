package com.jayesh.coronavirustracker.services;

        import com.jayesh.coronavirustracker.models.LocationStats;
        import org.apache.commons.csv.CSVFormat;
        import org.apache.commons.csv.CSVRecord;
        import org.springframework.scheduling.annotation.Scheduled;
        import org.springframework.stereotype.Service;

        import javax.annotation.PostConstruct;
        import java.io.IOException;
        import java.io.StringReader;
        import java.net.URI;
        import java.net.http.HttpClient;
        import java.net.http.HttpRequest;
        import java.net.http.HttpResponse;
        import java.util.ArrayList;
        import java.util.List;

@Service
public class CoronaVirusDataService {

    //private static final String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private static final String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    //when method in  @Service gets run , run method inside @postconstruct
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        List<LocationStats> newStats = new ArrayList<>(); //reason of doing this & not clearing the above one is  Concurrency. There will be lot of people trying to access our services while we trying to construct this.



        //HttpClient: from java 11 onwards
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

       // System.out.println(httpResponse.body());

        //also added maven dependency into pom for parsing csv file
        //https://commons.apache.org/proper/commons-csv/user-guide.html
        //Reader in = new FileReader("path/to/file.csv");

        StringReader csvBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            //create a new instance, & populate here directly @28mins
            LocationStats locationStats = new LocationStats();
            //String state = record.get("Province/State");
            locationStats.setState(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));
            //locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            //System.out.println(state);
            //go in our models and write toString method
            //System.out.println(locationStats); //get rid of console logss
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDiffFromPrevDay(latestCases - prevDayCases);
            newStats.add(locationStats);

        }
        //at the end of method write this, it not completely concurrency proof, but good enough for now.
        this.allStats = newStats;
    }

}
