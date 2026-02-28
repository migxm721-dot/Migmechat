<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.5.2/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.5.2/build/container/assets/skins/sam/container.css" />
<script type="text/javascript" src="http://yui.yahooapis.com/2.5.2/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.5.2/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.5.2/build/container/container-min.js"></script>

<script>
		YAHOO.namespace("example.container");

		var con = YAHOO.example.container;
		var panelnum = "panel1";

		function init() {
			// Instantiate a Panel from markup
			YAHOO.example.container.panel1 = new YAHOO.widget.Panel(panelnum, { width:"320px", visible:false, constraintoviewport:true } );
		}

		function displayPanel(num) {

			var el = document.getElementById(num);
			var header = el.firstChild.innerHTML;
			if(!document.all) header = el.firstChild.nextSibling.innerHTML;
			var body = el.firstChild.nextSibling.innerHTML;
			if(!document.all) body = el.firstChild.nextSibling.nextSibling.nextSibling.innerHTML
			con.panel1.setBody('');
			con.panel1.setBody(body);
			con.panel1.setHeader('');
			con.panel1.setHeader(header);
			con.panel1.render();
			con.panel1.show();
		}

		YAHOO.util.Event.addListener(window, "load", init);
</script>


			<div style="font:bold 10px arial,helvetica,sansserif;">&nbsp;</div><p>
			<div style="font:bold 16px arial,helvetica,sansserif;">FAQ</div><p>
			<a href="javascript:displayPanel('faq1')">&#8226;What does the Western Union form look like?</a> <br>
			<a href="javascript:displayPanel('faq2')">&#8226;Where can I find a Western Union Agent?</a> <br>
			<a href="javascript:displayPanel('faq3')">&#8226;What is a Government Approved Photo ID?</a> <br>
			<a href="javascript:displayPanel('faq4')">&#8226;Are there fees?</a> <br>
			<a href="javascript:displayPanel('faq5')">&#8226;Can I request Western Union Payment from my mobile phone?</a> <br>
			<a href="javascript:displayPanel('faq7')">&#8226;What happens if I make a payment with Western Union and my account is not credited?</a> <br>
			<a href="javascript:displayPanel('faq8')">&#8226;Are there special conditions for using Western Union?</a> <br>
			<a href="javascript:displayPanel('faq9')">&#8226;What is the most I can send using Western Union?</a> <br>
			