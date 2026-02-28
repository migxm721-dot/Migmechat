<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//refresh session
if (!isPagelet()) {
	checkServerSessionStatus();
}
ice_check_session();
$page = $_GET['page'];
emitHeader();

/**
 * Instructions on adding news stories:
 *
 * 1. Create a link to the story on the default news page. You can copy an existing list item,
 *    just make sure you change the value of anyIdX to a unique ID (no spaces) e.g. apples, news-1-1-09
 * 2. Add your story to the top of the section marked 'ADD NEWS STORIES HERE'. You can copy the format of
 *    existing stories, but just make sure you have the following so it will display correctly:
 *      - start with 'case "yourId":' where yourId is the unique ID you set in step 1
 *		- special brackets around your HTML story. Start with '?>' and end with '<?php '
 *      - finish off with 'break;'
 *
 * Happy writing :)
 **/

// DEFAULT NEWS PAGE
if (empty($page)) {
	emitTitleWithBody("News", "mc-news");
	$backLink = "merchant_center.php";
	?>
    <div id="content">
        <div class="section">
            <h2>Recent News</h2>
            <ul>
                <li><a href="news.php?page=anyId1">News Item 1</a></li>
                <li><a href="news.php?page=anyId2">News Item 2</a></li>
                <li><a href="news.php?page=anyId3">News Item 3</a></li>
            </ul>
        </div>
        <!-- Uncomment the section below if you want a secondary section, e.g. for tips, etc. -->
        <!--
         <div class="section">
            <h2>Your Section Title</h2>
            <ul>
                <li><a href="news.php?page=yourId1">Secondary News Item 1</a></li>
                <li><a href="news.php?page=yourId2">Secondary News Item 2</a></li>
            </ul>
        </div>
        -->
    </div>
<?php
}else {
	emitTitleWithBody("News", "mc-news");
	$backLink = "news.php";
	?> <div id="content"> <?php

	// ADD NEWS STORIES HERE
	switch($page) {
		case "anyId1":
			?>
            <h2>News Item 1</h2>
            <p>Add your news story here. You can use basic HTML like <a href="">links</a> and even a list like the one below:</p>
            <ul>
                <li>List Item 1</li>
                <li>List Item 2</li>
                <li>List Item 3</li>
            </ul>
            <p>More example text with <strong>bold text</strong></p>
            <p>Even some foreign languages: Μη πάντως τοπικές διαχειριστής των, σε πάντα χαρτιού κειμένων όλη. Δε αρέσει λαμβάνουν εκτελέσει νέα, να ανά απλό ορίστε, τα πες δυστυχής χρησιμοποιούσες. Ώς μια δώσε αυτήν αποστηθίσει, κι βγαίνει χρησιμοποιούνταν ήδη. Όρο μα κόλπα βουτήξουν επιδιόρθωση, αν πόρτες θέλεις μια, μπουν πακέτο διοικητικό έχω ως. Ως εκθέσεις συγγραφής της, καθορίζουν ανακλύψεις περισσότερες στη μη.</p>
            <?php
			break;
		case "anyId2":
			?>
            <h2>News Item 2</h2>
            <p>Add your news story here. You can use basic HTML like <a href="">links</a> and even a list like the one below:</p>
            <ul>
                <li>List Item 1</li>
                <li>List Item 2</li>
                <li>List Item 3</li>
            </ul>
            <p>More example text with <strong>bold text</strong></p>
            <p>Even some foreign languages: Μη πάντως τοπικές διαχειριστής των, σε πάντα χαρτιού κειμένων όλη. Δε αρέσει λαμβάνουν εκτελέσει νέα, να ανά απλό ορίστε, τα πες δυστυχής χρησιμοποιούσες. Ώς μια δώσε αυτήν αποστηθίσει, κι βγαίνει χρησιμοποιούνταν ήδη. Όρο μα κόλπα βουτήξουν επιδιόρθωση, αν πόρτες θέλεις μια, μπουν πακέτο διοικητικό έχω ως. Ως εκθέσεις συγγραφής της, καθορίζουν ανακλύψεις περισσότερες στη μη.</p>
            <?php
			break;
		case "anyId3":
			?>
            <h2>News Item 3</h2>
            <p>Add your news story here. You can use basic HTML like <a href="">links</a> and even a list like the one below:</p>
            <ul>
                <li>List Item 1</li>
                <li>List Item 2</li>
                <li>List Item 3</li>
            </ul>
            <p>More example text with <strong>bold text</strong></p>
            <p>Even some foreign languages: Μη πάντως τοπικές διαχειριστής των, σε πάντα χαρτιού κειμένων όλη. Δε αρέσει λαμβάνουν εκτελέσει νέα, να ανά απλό ορίστε, τα πες δυστυχής χρησιμοποιούσες. Ώς μια δώσε αυτήν αποστηθίσει, κι βγαίνει χρησιμοποιούνταν ήδη. Όρο μα κόλπα βουτήξουν επιδιόρθωση, αν πόρτες θέλεις μια, μπουν πακέτο διοικητικό έχω ως. Ως εκθέσεις συγγραφής της, καθορίζουν ανακλύψεις περισσότερες στη μη.</p>
            <?php
			break;
	}
	?> </div> <?php
}
emitFooter($backLink, "", "merchant", "");
?>
</body>
</html>
