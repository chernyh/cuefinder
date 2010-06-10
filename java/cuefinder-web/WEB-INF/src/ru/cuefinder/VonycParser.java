package ru.cuefinder;

/**
 * Created by IntelliJ IDEA.
 * User: chernyh
 * Date: 05.06.2010
 * Time: 2:41:46
 * To change this template use File | Settings | File Templates.
 */
public class VonycParser extends CueFinder
{
    public VonycParser( String mp3Filename )
    {
        super( mp3Filename );
    }

    @Override
    protected String get_radioshow_folder_path()
    {
        return "?page=cues&folder=vonyc";
    }

    @Override
    protected String parse_url_to_cue_file( String html, String asotNo, int part_no )
    {
        return getFirstMatch( "(download.php[?]type=cue.*vonyc_sessions_" + asotNo + "-.*\\.cue)\\\"\\>\\<img", html );
    }


    public String parse_release_no() throws Exception
    {
        release_no = getFirstMatch( "sessions_([0-9]+)-", mp3Filename );
        log.add( "Vonic release is '" + release_no + "'" );
        return release_no;

    }


}