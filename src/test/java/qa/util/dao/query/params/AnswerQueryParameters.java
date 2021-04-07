package qa.util.dao.query.params;

import java.util.Date;

public final class AnswerQueryParameters {

    private AnswerQueryParameters() {
    }

    public static final Boolean ANSWERED = false;
    public static final Date DATE = new Date(99999999999999L);
    public static final Long QUESTION_ID = 1L;
    public static final String TEXT =
            """
            <div id="map_canvas"></div>
            <script>
              var ib = new InfoBox();
              var markers = [];
                        
              function initialize() {
                        
                var mapOptions = {
                  zoom: 12,
                  center: new google.maps.LatLng(52.204872, 0.120163),
                  mapTypeId: google.maps.MapTypeId.ROADMAP,
                  //  styles: styles,
                  scrollwheel: false
                };
                var map = new google.maps.Map(document.getElementById('map_canvas'), mapOptions);
                        
                google.maps.event.addListener(map, "click", function() {
                  ib.close()
                });
                        
                setMarkers(map, sites);
                // open first marker's infowindow
                google.maps.event.trigger(markers[0], 'click');
                        
                infowindow = new google.maps.InfoWindow({
                  content: "loading..."
                        
                });
              }
            """;
    public static final String SECOND_TEXT =
            """
            async downloadODSFile(id) {
                await this.requestService.downloadODSFile(id).subscribe(res => {
                    console.log(res.body) //Byte array
                });
            }
            """;
}
