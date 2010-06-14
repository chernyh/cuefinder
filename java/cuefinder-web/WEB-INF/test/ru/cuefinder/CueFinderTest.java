package ru.cuefinder;

import junit.framework.TestCase;
import ru.cuefinder.ASOTParser;
import ru.cuefinder.CueFinder;
import ru.cuefinder.CueFinderFactory;
import ru.cuefinder.MagicIslandParser;
import ru.cuefinder.MarkusShultzParser;
import ru.cuefinder.TiestoParser;
import ru.cuefinder.VonycParser;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CueFinderTest extends TestCase
{

    String getFirstMatch( String regex, String input )
    {
        Pattern p = Pattern.compile( regex );
        Matcher m = p.matcher( input );
        if( m.find() )
        {
            return m.group( 1 );
        } else
        {
            return null;
        }

    }

    public void test_getFirstMatch() throws Exception
    {
        String file_name = "Markus Schulz presents - Global DJ Broadcast WMC Special (25 March 2010).mp3";
        assertEquals( "25 March 2010", getFirstMatch( "\\((.*)\\)", file_name ) );
    }

    public void testParse() throws ParseException
    {
        SimpleDateFormat df3 = new SimpleDateFormat( "MMMM", Locale.US );
        Date d = df3.parse( "March" );
    }

    public void test_ParseGDJBRelease2() throws Exception
    {
        String file_name = "Markus Schulz presents - Global DJ Broadcast WMC Special (25 March 2010).mp3";
        CueFinder cf = new MarkusShultzParser( file_name );
        String rel_no = cf.parse_release_no();
        assertEquals( "25-03-2010", rel_no );

    }

    public void test_ParseGDJBRelease3() throws Exception
    {
        String file_name = "Markus_Schulz_presents_-_Global_DJ_Broadcast_World_Tour_1_April_2010.mp3";
        CueFinder cf = new MarkusShultzParser( file_name );
        String rel_no = cf.parse_release_no();
        assertEquals( "01-04-2010", rel_no );

    }

    public void test_ParseGDJBRelease4() throws Exception
    {
        String file_name = "Global DJ Broadcast (2010-05-27) (Do You Dream? Album Release Special) [TMB] (SBD).mp3";
        CueFinder cf = new MarkusShultzParser( file_name );
        String rel_no = cf.parse_release_no();
        assertEquals( "27-05-2010", rel_no );
    }

    public void test_CueFinderFactory()
    {
        String file_name = "Markus Schulz presents - Global DJ Broadcast WMC Special (25 March 2010).mp3";
        assertTrue( CueFinderFactory.makeCueFinder( file_name ) instanceof MarkusShultzParser );

        file_name = "Armin van Buuren presents - A State of Trance Episode 430.mp3";
        assertTrue( CueFinderFactory.makeCueFinder( file_name ) instanceof ASOTParser );

        file_name = "Roger Shah presents Magic Island - Music for Balearic People Episode 098.mp3";
        assertTrue( CueFinderFactory.makeCueFinder( file_name ) instanceof MagicIslandParser );

        file_name = "01-paul_van_dyk_-_vonyc_sessions_188-sat-04-01-2010-talion.mp3";
        assertTrue( CueFinderFactory.makeCueFinder( file_name ) instanceof VonycParser );

    }

    public void test_MagicIsland_parse_release_no() throws Exception
    {
        String file_name = "Roger Shah presents Magic Island - Music for Balearic People Episode 098.mp3";
        MagicIslandParser cf = new MagicIslandParser( file_name );
        String rel_no = cf.parse_release_no();
        assertEquals( "098", rel_no );
    }


    public void test_MagicIsland_parse_cue_url() throws Exception
    {
        String file_name = "Roger Shah presents Magic Island - Music for Balearic People Episode 098.mp3";
        MagicIslandParser cf = new MagicIslandParser( file_name );
        String html = "<a href=\"download.php?type=cue&folder=magicisland&" +
                "filename=01-roger_shah_magic_island_-_music_for_balearic_people_098_%28di.fm%29_26-03-2010-tt.cue\"><img src=\"layout/download.png\" alt=\"Download!\" /></a> <a href=\"?page=tracklist&folder=magicisland&filename=01-roger_shah_magic_island_-_music_for_balearic_people_098_%28di.fm%29_26-03-2010-tt.cue\"> Magic Island - Music for Balearic People 098 (2010-03-26) [TT]</a><br />";
        String url = cf.find_url_to_cue_file( html );
        assertEquals( "download.php?type=cue&folder=magicisland&" +
                "filename=01-roger_shah_magic_island_-_music_for_balearic_people_098_%28di.fm%29_26-03-2010-tt.cue", url );

    }

    public void test_MagicIsland_parse_cue_url2() throws Exception
    {
        String file_name = "Roger Shah presents Magic Island - Music for Balearic People Episode 99.mp3";
        MagicIslandParser cf = new MagicIslandParser( file_name );
        String html = "<a href=\"download.php?type=cue&folder=magicisland&" +
                "filename=01-DJ_Shah_-_Magic_Island_-_Music_for_Balearic_People_99-NET-2010-04-02-iRUSH.cue\"><img src=\"layout/download.png\" alt=\"Download!\" /></a> <a href=\"?page=tracklist&folder=magicisland&filename=01-DJ_Shah_-_Magic_Island_-_Music_for_Balearic_People_99-NET-2010-04-02-iRUSH.cue\"> Magic Island - Music for Balearic People 099 (2010-04-02) [iRUSH]</a><br /> ";
        String url = cf.find_url_to_cue_file( html );
        assertEquals( "download.php?type=cue&folder=magicisland&" +
                "filename=01-DJ_Shah_-_Magic_Island_-_Music_for_Balearic_People_99-NET-2010-04-02-iRUSH.cue", url );

    }

    public void test_VonycReleaseNo() throws Exception
    {
        String file_name = "01-paul_van_dyk_-_vonyc_sessions_189-sat-04-08-2010-talion.mp3";
        VonycParser cf = new VonycParser( file_name );
        String rel_no = cf.parse_release_no();
        assertEquals( "189", rel_no );
    }

    public void test_vonyc_parse_cue_url() throws Exception
    {
        String file_name = "01-paul_van_dyk_-_vonyc_sessions_188-sat-04-01-2010-talion.mp3";
        VonycParser cf = new VonycParser( file_name );
        String html = "<p class=\"list\">\n" +
                "<a href=\"download.php?type=cue&folder=vonyc&" +
                "filename=01-paul_van_dyk_-_vonyc_sessions_188-sat-04-01-2010-talion.cue\"><img";
        String url = cf.find_url_to_cue_file( html );
        assertEquals( "download.php?type=cue&folder=vonyc&" +
                "filename=01-paul_van_dyk_-_vonyc_sessions_188-sat-04-01-2010-talion.cue", url );

    }


    public void test_ASOT_parse_cue_url() throws Exception
    {
        String file_name = "Armin van Buuren presents - A State of Trance Episode 455.mp3";
        ASOTParser cf = new ASOTParser( file_name );
        String html = "<a href=\"download.php?type=cue&amp;folder=asot&amp;filename=01-Armin_van_Buuren_-_A_State_of_Trance_456-NET-2010-05-13-iRUSH.cue\"><img src=\"layout/download.png\" alt=\"Download!\" /></a> <a href=\"?page=tracklist&amp;folder=asot&amp;filename=01-Armin_van_Buuren_-_A_State_of_Trance_456-NET-2010-05-13-iRUSH.cue\"> A State of Trance Episode 456 (2010-05-13) [iRUSH]</a><br />\n" +
                "<a href=\"download.php?type=cue&amp;folder=asot&amp;filename=01-armin_van_buuren_-_a_state_of_trance_455_%28di.fm%29_06-05-2010-tt.cue\"><img src=\"layout/download.png\" alt=\"Download!\" /></a> <a href=\"?page=tracklist&amp;folder=asot&amp;filename=01-armin_van_buuren_-_a_state_of_trance_455_%28di.fm%29_06-05-2010-tt.cue\"> A State of Trance Episode 455 (2010-05-06) [TT]</a><br />\n" +
                "<a href=\"download.php?type=cue&amp;folder=asot&amp;filename=01-armin_van_buuren_-_a_state_of_trance_episode_455-%28di.fm%29-06-05-2010-ps.cue\"><img src=\"layout/download.png\" alt=\"Download!\" /></a> <a href=\"?page=tracklist&amp;folder=asot&amp;filename=01-armin_van_buuren_-_a_state_of_trance_episode_455-%28di.fm%29-06-05-2010-ps.cue\"> A State of Trance Episode 455 (2010-05-06) [PS]</a><br />";
        String url = cf.find_url_to_cue_file( html );
        assertEquals( "download.php?type=cue&folder=asot&filename=01-armin_van_buuren_-_a_state_of_trance_455_%28di.fm%29_06-05-2010-tt.cue", url );

    }

    public void testEncoding() throws Exception
    {
//        Map m = Charset.availableCharsets();
        TiestoParser cf = new TiestoParser( "/home/chernyh/tmp/Club Life 167 (2010-06-11) part 1 [TALiON].mp3" );
            cf.download_cue();
    }


}
