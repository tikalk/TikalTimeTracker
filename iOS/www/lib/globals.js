// JavaScript Document
// 
// Global Variables
var root = this;
var fsclient
var currentLocation;
var currentLandL;
var nearbyLocations;
var fsAPI_KEY = "1OYPMZW55HEI5CHOJ0AH4EGJATOF0TQD3Z03PRNAJZIKWTPM";
var fsAPI_SECRET = "HG4IHVAI4E01RFR135PLJ5TERNKYTDAGQWG0VUSRWEGIKLIG";

// Persistence Models
var Project = persistence.define('Project', {
  fid: "TEXT",
  name: "TEXT",
  address: "TEXT",
  latitude: "TEXT",
  longitude: "TEXT",
  currentlyHere: "BOOL"
});

$('#mainPage').live("pageshow", function() {
	// Load Current Projects from WebSQL Database and refresh Listview
	console.log("main page show");
    persistence.store.websql.config(persistence, 'tikaltimetracker', 'Tikal Time Tracker DB', 5 * 1024 * 1024);
	persistence.schemaSync(function(tx) { 
		var projects = Project.all(); // Returns QueryCollection of all Projects in Database
		var projectsJSONString = "";
		projects.list(null, function (results) {
			var list = $( "#mainPage" ).find( ".lstMyProjects" );
			//Empty current list
	        list.empty();
			//Use template to create items & add to list
			$( "#projectItem" ).tmpl( results ).appendTo( list );
			//Call the listview jQuery UI Widget after adding 
			//items to the list allowing correct rendering
			list.listview( "refresh" );
			console.log("refresh project listview");
		});
	});                
});

$('#projectOptions').live("pageshow", function() {
	$( "#projectOptions" ).find( ".ui-title" ).html("Loading Project...");
	
	Project.findBy("fid", $.mobile.pageData.fid, function(project) {
		$( "#projectOptions" ).find( ".ui-title" ).html(project.name);
	});               
});
						
$('#map_page').live("pageshow", function() {
	$('#map_canvas').gmap(
		{ 'center' : new google.maps.LatLng(currentLocation.location.lat, currentLocation.location.lng), 
		  'mapTypeControl' : true
		}
	);
                    
	$('#map_canvas').gmap('refresh');
    
    
	$('#map_canvas').gmap('clear', 'markers');

	var marker = $('#map_canvas').gmap(
		'addMarker',{ id:'m_1', 'position':  new google.maps.LatLng(currentLocation.location.lat, currentLocation.location.lng), 'bounds': true , 'animation' : 	google.maps.Animation.DROP}
	)
	.click(function() {
		$('#map_canvas').gmap('openInfoWindow', { 'content': currentLocation.name }, this);
	});
});