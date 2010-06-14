package ru.cuefinder;

/**
 * Created by IntelliJ IDEA.
 * User: chernyh
 * Date: 05.06.2010
 * Time: 2:41:46
 * To change this template use File | Settings | File Templates.
 */
public class TiestoParser extends CueFinder
{
    public TiestoParser( String mp3Filename )
    {
        super( mp3Filename );
    }

    @Override
    protected String getForcedCueCharset()
    {
        return "ISO-8859-15";
    }

    @Override
    protected String get_radioshow_folder_path()
    {
        return "?page=cues&folder=clublife";
    }

    @Override
    protected String parse_url_to_cue_file( String html, String asotNo, int part_no )
    {
        return getFirstMatch( "(download.php[?]type=cue.*filename=0" + part_no + "-tiesto_-_club_life_" + asotNo + "-.*\\.cue)\\\"\\>\\<img", html );
    }


    public String parse_release_no() throws Exception
    {
        release_no = getFirstMatch( "Club Life ([0-9]+) ", mp3Filename );
        log.add( "Tiesto release is '" + release_no + "'" );
        return release_no;

    }

    @Override
    protected int parse_part_no()
    {
        String s = getFirstMatch( "part ([0-9]+)", mp3Filename );
        part_no = Integer.parseInt( s );
        return part_no;
    }
}