package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import Account.User;
import Article.Article;
import Article.ArticleCategorizer;

public class Database {

    private User user = null;

    private static final String url = "jdbc:sqlite:articles.db";

    // Lists to store articles fetched from the database
    private List<Article> articles = new ArrayList<>();
    private List<Article> articlesInput = new ArrayList<>();

    // Getter for the list of articles with simplified input data
    public List<Article> getArticlesInput() {
        return this.articlesInput;
    }

    // Setter method to get the user instance
    public void setUser(User user) {
        this.user = user;
    }

    // Method to create the users table in the database if it doesn't already exist
    public static void UsersTableMaker() {
        // SQL statement to create the "users" table with required columns and constraints
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "loginType TEXT," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT," +
                "username TEXT)";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // Execute the table creation statement
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            // Handle any errors during database setup
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }

    // Fetch all articles from the database and populate the articles and articlesInput lists
    public List<Article> fetchAllArticles() {

        String selectSQL = "SELECT * FROM articles"; // SQL query to retrieve all articles

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            // Iterate through the result set and create Article objects
            while (rs.next()) {
                String headline = rs.getString("headline");
                String desc = rs.getString("description");
                String authors = rs.getString("authors");
                String category = rs.getString("category");
                String link = rs.getString("url");
                String date = rs.getString("date");
                int id = rs.getInt("articleID");

                // Create an Article object with full details
                Article article = new Article(headline, desc, authors, category, link, date, id);

                // Create a simplified Article object for input processing
                Article articleInput = new Article(headline, desc, category, 0, url);

                // Add articles to their respective lists
                this.articles.add(article);
                this.articlesInput.add(articleInput);
            }

        } catch (SQLException e) {
            // Log errors encountered while fetching articles
            System.out.println("Error fetching articles: " + e.getMessage());
        }

        return this.articles; // Return the full list of articles
    }

    // Method to create the perferences table to store user-article interactions
    public static void PreferenceTableMaker() {
        // SQL statement to create the "preferences" table with foreign keys referencing "users" and "articles" tables
        String createTableSQL = """
        CREATE TABLE IF NOT EXISTS preferences (
        userID INTEGER NOT NULL,
        articleID INTEGER NOT NULL,
        rating INTEGER NOT NULL,
        PRIMARY KEY (userID, articleID),
        FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (articleID) REFERENCES articles(articleID) ON DELETE CASCADE ON UPDATE CASCADE
    )
    """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // Execute the table creation statement
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            // Handle any errors during database setup
            System.err.println("Database setup failed: " + e.getMessage());
        }
    }

    // Method to create the "articles" table and initialize it with data
    public static void ArticlesTableMaker() {
        // SQL statement to create the "articles" table with relevant columns
        String createTableSQL = "CREATE TABLE IF NOT EXISTS articles (" +
                "articleID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "headline TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "authors TEXT," +
                "category TEXT," +
                "predicted TEXT," +
                "url TEXT," +
                "date TEXT)";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // Execute the table creation statement
            stmt.execute(createTableSQL);
            // Call a separate method to populate the articles table
            insertToArticles();
        } catch (SQLException e) {
            // Handle any errors during database setup
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }

    public static void insertToArticles() {
        if (isArticleTableEmpty()) {
            // SQL command to insert data into the articles table
            String insertSQL = "INSERT INTO articles (headline, description, authors, category, predicted, url, date) VALUES (?, ?, ? , ?, ?, ?, ?)";

            String[][] articles = {
                    {"23 Of The Funniest Tweets About Cats And Dogs This Week (Sept. 17-23)", "\"Until you have a dog you don't understand what could be eaten.\"", "Elyse Wanshel", "COMEDY", "", "https://www.huffpost.com/entry/funniest-tweets-cats-dogs-september-17-23_n_632de332e4b0695c1d81dc02", "2022-09-23"},
                    {"23 Of The Funniest Tweets About Cats And Dogs This Week (July 16-22)", "\"you ever bring ur pet up to a mirror and ur like ‘that's you’\"", "Elyse Wanshel", "COMEDY", "", "https://www.huffpost.com/entry/funniest-tweets-cats-dogs-july-16-22_n_62d5913fe4b092a3f6bf2772", "2022-07-22"},
                    {"20 Of The Funniest Tweets About Cats And Dogs This Week (June 18-24)", "\"Petition to stop ringing the doorbell on TV so my dog can lead a less confusing life\"", "Elyse Wanshel", "COMEDY", "", "https://www.huffpost.com/entry/funniest-tweets-cats-dogs-june-18-24_n_62b5d48fe4b0c77098bb5cc6", "2022-06-25"},
                    {"Seth Meyers Has A Field Day With Rudy Giuliani's 'Diet Pepsi' Claim", "\"Sorry, buddy. You just gave yourself away. No one’s favorite drink is Diet Pepsi,\" the \"Late Night\" host said.", "Josephine Harvey", "COMEDY", "", "https://www.huffpost.com/entry/seth-meyers-rudy-giuliani-diet-pepsi_n_62aa8553e4b0c77098a98864", "2022-06-16"},
                    {"25 Of The Funniest Tweets About Cats And Dogs This Week (June 4-10)", "\"i keep hearing this ad for fresh cat food that says 'your cute kitty is descended from fierce desert cats' and then i look at my cat and say 'this cat??'\"", "Elyse Wanshel", "COMEDY", "", "https://www.huffpost.com/entry/funniest-tweets-cats-dogs-june-4-10_n_62a374bfe4b0c77098a025a2", "2022-06-10"},
                    {"REI Workers At Berkeley Store Vote To Unionize In Another Win For Labor", "They follow in the footsteps of REI workers in New York City who formed a union earlier this year.", "Dave Jamieson", "BUSINESS", "", "https://www.huffpost.com/entry/rei-workers-berkeley-store-union_n_6307a5f4e4b0f72c09ded80d", "2022-08-25"},
                    {"Twitter Lawyer Calls Elon Musk 'Committed Enemy' As Judge Sets October Trial", "Delaware Chancery Judge Kathaleen McCormick dealt the world's richest person a setback in ordering a speedy trial on his abandoned deal to buy Twitter.", "Marita Vlachou", "BUSINESS", "", "https://www.huffpost.com/entry/twitter-elon-musk-trial-october_n_62d7c115e4b000da23f9c7df", "2022-07-20"},
                    {"Starbucks Leaving Russian Market, Shutting 130 Stores", "Starbucks' move follows McDonald's exit from the Russian market last week.", "DEE-ANN DURBIN, AP", "BUSINESS", "", "https://www.huffpost.com/entry/starbucks-leaves-russian-market-shuts-stores_n_628b9804e4b05cfc268f4413", "2022-05-23"},
                    {"Crypto Crash Leaves Trading Platform Coinbase Slumped", "Cryptocurrency trading platform Coinbase has lost half its value in the past week.", "Matt Ott, AP", "BUSINESS", "", "https://www.huffpost.com/entry/coinbase-crypto-slumping_n_627c5582e4b0b74b0e7ed621", "2022-05-12"},
                    {"US Added 428,000 Jobs In April Despite Surging Inflation", "At 3.6%, unemployment nearly reached the lowest level in half a century.", "Paul Wiseman, AP", "BUSINESS", "", "https://www.huffpost.com/entry/us-april-jobs-report-2022_n_627517dfe4b009a811c295ec", "2022-05-06"},
                    {"Maury Wills, Base-Stealing Shortstop For Dodgers, Dies At 89", "Maury Wills, who helped the Los Angeles Dodgers win three World Series titles with his base-stealing prowess, has died.", "Beth Harris, AP", "SPORTS", "", "https://www.huffpost.com/entry/dodgers-baseball-obit-wills_n_6329feb3e4b07198f0134500", "2022-09-20"},
                    {"Las Vegas Aces Win First WNBA Title, Chelsea Gray Named MVP", "Las Vegas never had a professional sports champion — until Sunday.", "Pat Eaton-Robb, AP", "SPORTS", "", "https://www.huffpost.com/entry/2022-wnba-finals_n_6327f56fe4b0eac9f4e3144d", "2022-09-19"},
                    {"Boston Marathon To Make Race More Inclusive For Nonbinary Runners", "The race's organizers say nonbinary athletes won't have to register with the men's or women's divisions and provided qualifying times to guide their training.", "Sanjana Karanth", "SPORTS", "", "https://www.huffpost.com/entry/boston-marathon-nonbinary-runners_n_631fade4e4b046aa0237a055", "2022-09-12"},
                    {"Anthony Varvaro, MLB Pitcher Turned Transit Cop, Dies In Crash On Way To 9/11 Ceremony", "Varvaro pitched mostly with the Atlanta Braves and started his law enforcement career in 2016.", "", "SPORTS", "", "https://www.huffpost.com/entry/anthony-varvaro-dead_n_631f6827e4b027aa405e1899", "2022-09-12"},
                    {"Carlos Alcaraz Wins U.S. Open For 1st Slam Title, Top Ranking", "Carlos Alcaraz defeated Casper Ruud in the U.S. Open final to earn his first Grand Slam at age 19 and become the youngest man to move up to No. 1 in the rankings.", "HOWARD FENDRICH, AP", "SPORTS", "", "https://www.huffpost.com/entry/carlos-alcaraz-us-open-grand-slam-win_n_631e7452e4b027aa405cf51b", "2022-09-11"},
                    {"Lovely Honeymoon Destinations In The U.S.", "Newlyweds don't have to travel too far for a relaxing, scenic vacation.", "Caroline Bologna", "TRAVEL", "", "https://www.huffpost.com/entry/honeymoon-destinations-united-states_l_62445c87e4b0e44de9bbfb72", "2022-04-07"},
                    {"How To Salvage Your Vacation If It Rains Most Of The Time", "Travel experts share their advice for finding the bright side of gloomy vacation weather.", "Caroline Bologna", "TRAVEL", "", "https://www.huffpost.com/entry/how-to-make-rainy-vacations-enjoyable_l_61f2070fe4b04f9a12b881c4", "2022-02-03"},
                    {"A Definitive Guide To Airplane Seat Etiquette", "Experts explain what you should know about reclining your seat, sharing armrests and more.", "Caroline Bologna", "TRAVEL", "", "https://www.huffpost.com/entry/airplane-seat-etiquette_l_61b7e8c4e4b0490e9bd740a4", "2021-12-16"},
                    {"The Rudest Things You Can Do At A Hotel", "Etiquette experts share faux pas to avoid while staying at a hotel, especially during the pandemic.", "Caroline Bologna", "TRAVEL", "", "https://www.huffpost.com/entry/rudest-things-you-can-do-hotel_l_615b23dfe4b050254235fa82", "2021-10-07"},
                    {"Passport Wait Times Are Awful Right Now. Here's What To Do.", "Experts share their advice for making the passport renewal process less stressful.", "Caroline Bologna", "TRAVEL", "", "https://www.huffpost.com/entry/passport-renew-wait-time-pandemic_l_61143e13e4b0c2db778c867d", "2021-08-24"},
                    {"First Public Global Database Of Fossil Fuels Launches", "On Monday, the world’s first public database of fossil fuel production, reserves and emissions launches.", "Drew Costley, AP", "ENVIRONMENT", "", "https://www.huffpost.com/entry/oil-gas-coal-reserves-database_n_6327a81ae4b0eac9f4e2fd23", "2022-09-18"},
                    {"Alaska Prepares For 'Historic-Level' Storm Barreling Towards Coast", "In 10 years, people will be referring to the September 2022 storm as a benchmark storm.", "BECKY BOHRER, MARK THIESSEN and JOHN ANTCZAK, AP", "ENVIRONMENT", "", "https://www.huffpost.com/entry/bc-us-alaska-coastal-storm_n_6325f39fe4b0eac9f4e25cbc", "2022-09-17"},
                    {"Puerto Rico Braces For Landslides And Severe Flooding As Tropical Storm Fiona Approaches", "Puerto Rico was under a hurricane watch Saturday as the storm barreled towards the U.S. territory.", "DÁNICA COTO, AP", "ENVIRONMENT", "", "https://www.huffpost.com/entry/tropical-storm-fiona-puerto-rico_n_6325d372e4b0ed021dfd719c", "2022-09-17"},
                    {"Privatization Isn’t The Answer To Jackson’s Water Crisis", "Studies have repeatedly shown that ending public administration of water supplies doesn’t work — but that’s now on the table in Mississippi.", "Nathalie Baptiste", "ENVIRONMENT", "", "https://www.huffpost.com/entry/jackson-water-crisis-privatization_n_6324d6c2e4b0ed021dfd034f", "2022-09-17"},
                    {"Severe Winds Batter Southern California As Heat Wave Breaks", "After a 10-day heat wave that nearly overwhelmed the electrical grid, Southern California got cooler weather but also a ferocious tropical storm.", "JULIE WATSON and JOHN ANTCZAK, AP", "ENVIRONMENT", "", "https://www.huffpost.com/entry/bc-us-california-wildfires_n_631c9b62e4b000d988519ee3", "2022-09-10"},
                    {"Twitch Bans Gambling Sites After Streamer Scams Folks Out Of $200,000", "One man's claims that he scammed people on the platform caused several popular streamers to consider a Twitch boycott.", "Ben Blanchet", "TECH", "", "https://www.huffpost.com/entry/twitch-streamers-threaten-strike-gambling_n_632a72bce4b0cd3ec2628b20", "2022-09-21"},
                    {"TikTok Search Results Riddled With Misinformation: Report", "A U.S. firm that monitors false online claims reports that searches for information about prominent news topics on TikTok are likely to turn up results riddled with misinformation.", "DAVID KLEPPER, AP", "TECH", "", "https://www.huffpost.com/entry/bc-us-tiktok-misinformation_n_6321c9b0e4b027aa40614814", "2022-09-14"},
                    {"Cloudflare Drops Hate Site Kiwi Farms", "Cloudflare CEO Matthew Prince had previously resisted calls to block the site.", "The Associated Press, AP", "TECH", "", "https://www.huffpost.com/entry/cloudflare-kiwi-farms_n_6315993ae4b0eac9f4ce0ba1", "2022-09-05"},
                    {"Instagram And Facebook Remove Posts Offering Abortion Pills", "Facebook and Instagram began removing some of these posts, just as millions across the U.S. were searching for clarity around abortion access.", "Amanda Seitz, AP", "TECH", "", "https://www.huffpost.com/entry/instagram-facebook-remove-posts-offering-abortion-pills_n_62bac62fe4b080fb6709dfbe", "2022-06-28"},
                    {"Google Engineer On Leave After He Claims AI Program Has Gone Sentient", "Artificially intelligent chatbot generator LaMDA wants “to be acknowledged as an employee of Google rather than as property,” says engineer Blake Lemoine.", "Mary Papenfuss", "TECH", "", "https://www.huffpost.com/entry/blake-lemoine-lamda-sentient-artificial-intelligence-google_n_62a5613ee4b06169ca8c0a2e", "2022-06-12"},
                    {"Golden Globes Returning To NBC In January After Year Off-Air", "For the past 18 months, Hollywood has effectively boycotted the Globes after reports that the HFPA’s 87 members of non-American journalists included no Black members.", "", "ENTERTAINMENT", "", "https://www.huffpost.com/entry/golden-globes-return-nbc_n_6329f151e4b0ed991abda7f3", "2022-09-20"},
                    {"James Cameron Says He 'Clashed' With Studio Before 'Avatar' Release", "The 'Avatar' director said aspects of his 2009 movie are 'still competitive with everything that’s out there these days.'", "Ben Blanchet", "ENTERTAINMENT", "", "https://www.huffpost.com/entry/james-cameron-fought-studio-avatar_n_63268723e4b046aa02400678", "2022-09-18"},
                    {"Amazon Greenlights 'Blade Runner 2099' Limited Series Produced By Ridley Scott", "The director of the original 1982 film joins a writer of the 2017 sequel for the newest installment in the sci-fi franchise.", "Marco Margaritoff", "ENTERTAINMENT", "", "https://www.huffpost.com/entry/blade-runner-2099-series-announced_n_63247adfe4b027aa40656cc0", "2022-09-16"},
                    {"The Phantom Of The Opera' To Close On Broadway Next Year", "“The Phantom of the Opera” — Broadway’s longest-running show — is scheduled to close in February 2023, a victim of post-pandemic softening in theater attendance in New York.", "Mark Kennedy, AP", "ENTERTAINMENT", "", "https://www.huffpost.com/entry/the-phantom-of-the-opera-to-close-on-broadway-next-year_n_6324ef40e4b082746bea3ce3", "2022-09-16"},
                    {"Viola Davis Feared A Heart Attack During 'The Woman King' Training", "The Oscar winner said she worked out for five hours a day for her role in the new action movie.", "Marco Margaritoff", "ENTERTAINMENT", "", "https://www.huffpost.com/entry/viola-davis-woman-king-heart-attack_n_6322f795e4b000d988585229", "2022-09-15"},
                    {"Biden Says U.S. Forces Would Defend Taiwan If China Invaded", "President issues vow as tensions with China rise.", "", "POLITICS", "", "https://www.huffpost.com/entry/biden-us-forces-defend-taiwan-against-china_n_6327ec68e4b0ed021dfe3695", "2022-09-19"},
                    {"‘Beautiful And Sad At The Same Time’: Ukrainian Cultural Festival Takes On A Deeper Meaning This Year", "An annual celebration took on a different feel as Russia's invasion dragged into Day 206.", "Jonathan Nicholson", "POLITICS", "", "https://www.huffpost.com/entry/ukraine-festival_n_6327b4a0e4b082746beb52c7", "2022-09-19"},
                    {"Biden Says Queen's Death Left 'Giant Hole' For Royal Family", "U.S. President Joe Biden, in London for the funeral of Queen Elizabeth II, says his heart went out to the royal family, adding the queen’s death left a “giant hole.”.", "Darlene Superville, AP", "POLITICS", "", "https://www.huffpost.com/entry/europe-britain-royals-biden_n_63276eabe4b046aa02406a13", "2022-09-18"},
                    {"Bill To Help Afghans Who Escaped Taliban Faces Long Odds In The Senate", "Republican outrage over the shoddy U.S. withdrawal from Afghanistan hasn’t spurred support for resettling refugees.", "Hamed Ahmadi and Arthur Delaney", "POLITICS", "", "https://www.huffpost.com/entry/afghan-adjustment-act-congress_n_6324ad6ee4b027aa4065ebef", "2022-09-16"},
                    {"Mark Meadows Complies With Justice Dept. Subpoena: Report", "The former White House chief of staff has turned over records as part of a federal investigation into the Jan. 6, 2021 assault on the Capitol.", "ERIC TUCKER, AP", "POLITICS", "", "https://www.huffpost.com/entry/capitol-riot-investigation-mark-meadows_n_63235733e4b000d988594a5d", "2022-09-15"},
                    {"Memphis Police: Arrest Made In Jogger's Disappearance", "Police in Tennessee say an arrest has been made in the disappearance of a woman who was abducted while jogging.", "", "CRIME", "", "https://www.huffpost.com/entry/ap-us-jogger-abducted_n_6314aa89e4b0682ad3d1d6ed", "2022-09-04"},
                    {"Trump Org. CFO To Plead Guilty, Testify Against Company", "Allen Weisselberg is charged with taking more than $1.7 million in off-the-books compensation from the Trump Organization over several years.", "Michael R. Sisak, AP", "CRIME", "", "https://www.huffpost.com/entry/trump-org-cfo-to-plead-guilty-testify-against-company_n_62fd866ce4b0a85a8198706b", "2022-08-18"},
                    {"Officials: NH Missing Girl Case Shifts To Homicide Probe", "Authorities say the search for a New Hampshire girl who disappeared at age 5 in 2019 but was not reported missing until late last year is now considered a homicide investigation.", "Holly Ramer, AP", "CRIME", "", "https://www.huffpost.com/entry/united-states-missing-girl-new-hampshire_n_62f55349e4b045e6f6abcc27", "2022-08-11"},
                    {"Albuquerque Police Share Photo Of Car Eyed In Slayings Of Muslim Men", "Authorities have said that all four of the killings have several things in common and that they are looking to see if any other crimes could be related.", "Nina Golgowski", "CRIME", "", "https://www.huffpost.com/entry/albuquerque-volkswagen-suspect-muslim-shootings_n_62f150bde4b0da5ec0f6c579", "2022-08-08"},
                    {"Albuquerque Police Tell Muslim Community To Be 'Vigilant' Amid Series Of Murders", "Police are searching for the shooter, or shooters, believed to be responsible for a string of murders around the New Mexico city.", "Sara Boboltz", "CRIME", "", "https://www.huffpost.com/entry/albuquerque-new-mexico-muslim-community-murders_n_62eed8a3e4b09d09a2c46755", "2022-08-06"},
                    {"How To Hide Even The Worst Tan Lines, According To Makeup Pros", "Quick solutions to fix your farmer’s tan, from body makeup to self tanner.", "Julie Kendrick", "STYLE & BEAUTY", "", "https://www.huffpost.com/entry/how-to-hide-tan-lines_l_62e7edc2e4b09d14dc450969", "2022-08-08"},
                    {"Jello Skin: The Latest TikTok Skin Care Trend And How To Achieve It", "Jell-O is bouncy, jiggly and always snaps back after the slightest movement. According to the latest TikTok trend, your skin should be the same way.", "Rebecca Rovenstine", "STYLE & BEAUTY", "", "https://www.huffpost.com/entry/jello-skin-tiktok_l_62b5bc22e4b04a6173695b03", "2022-07-05"},
                    {"How To Treat Bug Bites The Way Dermatologists Do At Home", "Here's what to order now so you'll be itch-free all summer.", "Julie Kendrick", "STYLE & BEAUTY", "", "https://www.huffpost.com/entry/how-to-treat-bug-bites-dermatologists_l_629e5e54e4b090b53b882a74", "2022-06-16"},
                    {"How To Prevent And Treat Summer Chafing For Men", "Ditch the discomfort down there with these dermatologist-recommended tips.", "Katie McPherson", "STYLE & BEAUTY", "", "https://www.huffpost.com/entry/how-to-prevent-chafing-men_l_629113eae4b05cfc269a1baa", "2022-06-07"},
                    {"If Your Mole Looks Like This, It's Time To See A Dermatologist", "Experts explain the signs of melanoma to look out for on your skin.", "Kelsey Borresen", "STYLE & BEAUTY", "", "https://www.huffpost.com/entry/atypical-mole-dermatologist_l_628671b0e4b0acd09d25dd8d", "2022-05-23"}
            };

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

                // Loop through each article and add to the database
                for (String[] article : articles) {
                    pstmt.setString(1, article[0]); // headline
                    pstmt.setString(2, article[1]); // short_description
                    pstmt.setString(3, article[2]); // authors
                    pstmt.setString(4, article[3]); // category
                    pstmt.setString(5, article[4]); // predicted
                    pstmt.setString(6, article[5]); // url
                    pstmt.setString(7, article[6]); // date
                    pstmt.executeUpdate(); // Execute the insert
                }

            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE constraint failed")) {
                    System.out.println("Error: Duplicate entry for the article.");
                } else {
                    System.out.println("Error inserting article: " + e.getMessage());
                }
            }
        }
    }

    // Method to remove users with incomplete data from the "users" table
    public static void removeIncompleteUsers() {
        // SQL query to find users with missing or empty fields
        String selectQuery = "SELECT userID FROM users WHERE " +
                "loginType = '' OR email = '' OR password = '' OR username = ''";
        // SQL query to delete a user by their userID
        String deleteQuery = "DELETE FROM users WHERE userID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {
            // Loop through results and delete each incomplete user
            while (rs.next()) {
                int userID = rs.getInt("userID");
                try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                    pstmt.setInt(1, userID); // Set userID in the delete query
                    pstmt.executeUpdate(); // Execute deletion
                }
            }
        } catch (SQLException e) {
            // Handle errors during the search and delete process
            System.out.println("Error while searching and deleting incomplete records: " + e.getMessage());
        }
    }

    // Method to check if the "articles" table is empty
    public static boolean isArticleTableEmpty() {

        String query = "SELECT COUNT(*) FROM articles";
        boolean isEmpty = false;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Check the count of rows in the "articles" table
            if (rs.next()) {
                int count = rs.getInt(1);
                isEmpty = (count == 0); // If count is 0, the table is empty
            }
        } catch (Exception e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }

        return isEmpty; // Return whether the table is empty or not
    }

    // Method to update a user's preferences for an article
    public static void updateUserPreferences(int userID, Article article) {

        String insertSQL = "INSERT INTO preferences (userID, articleID, rating) VALUES (?, ?, ?)";
        String updateSQL = "UPDATE preferences SET rating = ? WHERE userID = ? AND articleID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement selectStmt = conn.prepareStatement("SELECT rating FROM preferences WHERE userID = ? AND articleID = ?");
             PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

            conn.setAutoCommit(false); // Disable auto-commit for transaction management

            // Check if the preference for this article already exists
            selectStmt.setInt(1, userID);
            selectStmt.setInt(2, article.getId());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // If a preference exists, update its rating
                updateStmt.setInt(1, article.getRating());
                updateStmt.setInt(2, userID);
                updateStmt.setInt(3, article.getId());
                updateStmt.executeUpdate();
            } else {
                // If no preference exists, insert a new one
                insertStmt.setInt(1, userID);
                insertStmt.setInt(2, article.getId());
                insertStmt.setInt(3, article.getRating());
                insertStmt.executeUpdate();
            }

            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Handle any errors during the update process
            System.err.println("Error updating preferences: " + e.getMessage());
        }
    }

    // Method to retrieve a user's preferences and match them to a list of articles
    public static List<Article> retrievePreferences(int userID, List<Article> articles) {

        String querySQL = "SELECT articleID, rating FROM preferences WHERE userID = ?";

        List<Article> preferences = new ArrayList<>(); // List to store articles with user preferences

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            pstmt.setInt(1, userID); // Set the userID in the query
            ResultSet rs = pstmt.executeQuery();

            // Loop through the result set to retrieve article preferences
            while (rs.next()) {
                int articleID = rs.getInt("articleID");
                int rating = rs.getInt("rating");

                // Match the articleID to an article in the provided list
                for (Article article : articles) {
                    if (article.getId() == articleID) {
                        article.setRating(rating); // Set the rating on the matching article
                        preferences.add(article); // Add the article to the preferences list
                        break; // Stop searching once a match is found
                    }
                }
            }
        } catch (SQLException e) {
            // Handle any errors during the retrieval process
            System.err.println("Error retrieving preferences: " + e.getMessage());
        }

        return preferences; // Return the list of articles with user preferences
    }

    public static User searchUser(String email) {
        User user = null;

        // Query to find the user based on the provided email
        String query = "SELECT userID, password, username, loginType FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                // If a record is found, populate the user object and exit the loop
                if (rs.next()) {
                    user = new User(rs.getString("username"), email, rs.getString("password"), rs.getString("loginType"));
                    user.setId(rs.getInt("userID"));
                } else {
                    System.out.println("       No account registered with this email. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("       An error occurred while accessing the database: " + e.getMessage());
        }
        return user;
    }

    // Inserts the email into the database and checks if it is unique
    public boolean insertEmail(String email) {
        String insertSQL = "INSERT INTO users (email, password, username, loginType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            conn.setAutoCommit(false); // Start transaction

            pstmt.setString(1, email);
            pstmt.setString(2, ""); // Default empty password
            pstmt.setString(3, ""); // Default empty username
            pstmt.setString(4, ""); // Default empty loginType

            pstmt.executeUpdate(); // Attempt to insert the email
            conn.commit(); // Commit the transaction
            return true; // Email inserted successfully

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                // Email already exists in the database
                return false;
            }
            System.out.println("Error inserting email: " + e.getMessage());
            return false;
        }
    }

    // Checks if the new email is unique in the database
    public boolean isEmailUnique(String newEmail) {
        String query = "SELECT email FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                return !rs.next(); // Return true if no matching email is found
            }
        } catch (SQLException e) {
            System.out.println("     Error checking email: " + e.getMessage());
            return false;
        }
    }

    // Inserts a new article into the database
    public void insertArticleToDatabase(Article article) {
        String insertSQL = "INSERT INTO articles (headline, description, authors, category, predicted, url, date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // Sets parameters for the prepared statement
            pstmt.setString(1, article.getHeadline());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getAuthors());
            pstmt.setString(4, article.getCategory());
            pstmt.setString(5, article.getCategory());
            pstmt.setString(6, article.getUrl());
            pstmt.setString(7, article.getDate());
            pstmt.executeUpdate(); // Executes the insertion query
            System.out.println("\n     Article added successfully!");

        } catch (SQLException e) {
            System.out.println("     Error adding article: " + e.getMessage());
        }

        // Triggers categorization after article is added
        ArticleCategorizer categorizer = new ArticleCategorizer();
        categorizer.Categorize();
    }

    // Method to retrieve article from the database
    public Article getArticle(int articleID) {
        String selectSQL = "SELECT * FROM articles WHERE articleID = ?";
        Article article = null;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setInt(1, articleID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    article = new Article(
                            rs.getString("headline"),
                            rs.getString("description"),
                            rs.getString("authors"),
                            rs.getString("predicted"),
                            rs.getString("url"),
                            rs.getString("date")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching article: " + e.getMessage());
        }

        return article;
    }

    // Updates the user's details in the database
    public void updateUser(String prevEmail) {
        String updateQuery = "UPDATE users SET username = ?, email = ?, password = ?, loginType = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Bind the updated user details to the query
            stmt.setString(1, this.user.getUsername());
            stmt.setString(2, this.user.getEmail());
            stmt.setString(3, this.user.getPassword());
            stmt.setString(4, this.user.getLoginType());
            stmt.setString(5, prevEmail);

            stmt.executeUpdate(); // Execute the update query

        } catch (SQLException e) {
            System.out.println("     Error updating user: " + e.getMessage());
        }
    }
}
