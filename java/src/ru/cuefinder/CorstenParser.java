package ru.cuefinder;

public class CorstenParser extends CueFinder
{
    public CorstenParser( String mp3Filename )
    {
        super( mp3Filename );
    }

    @Override
    protected String get_radioshow_folder_path()
    {
        return "?page=cues&folder=corstenscountdown";
    }


    public String parse_release_no()
    {
        String match = getFirstMatch( "([0-9]{3})", mp3Filename );

        log.add( "Corstens Countdown release is '" + match + "'" );
        return match;

    }

    @Override
    protected String parse_url_to_cue_file( String html, String relNo, int part_no )
    {
        release_no = getFirstMatch( "(download.php[?]type=cue.*_corstens_countdown_" + relNo + "_.*\\.cue)\\\"\\>\\<img", html );
        return release_no;
    }


}