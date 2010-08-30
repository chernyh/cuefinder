package ru.cuefinder;

public class AlyAndFilaParser extends CueFinder
{
    public AlyAndFilaParser( String mp3Filename )
    {
        super( mp3Filename );
    }

    @Override
    protected String get_radioshow_folder_path()
    {
        return "?page=cues&folder=fsoe";
    }


    public String parse_release_no()
    {
        String match = getFirstMatch( "([0-9]{3})", mp3Filename );

        log.add( "Future Sound of Egypt  release is '" + match + "'" );
        return match;

    }

    @Override
    protected String parse_url_to_cue_file( String html, String relNo, int part_no )
    {
        release_no = getFirstMatch( "(download.php[?]type=cue.*fsoe.*" + relNo + ".*\\.cue)\\\"\\>\\<img", html );
        return release_no;
    }


}