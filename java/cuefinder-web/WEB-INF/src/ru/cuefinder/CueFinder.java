package ru.cuefinder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CueFinder
{
    String mp3Filename;
    String release_no;
    int part_no;
    String cue_file_name;

    public CueFinder( String mp3Filename )
    {
        this.mp3Filename = mp3Filename;
        try
        {
            this.release_no = parse_release_no();
        } catch( Exception e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.part_no = parse_part_no();

        File mp3File = new File( mp3Filename );
        File parent = mp3File.getParentFile();
        this.cue_file_name = ( parent == null ? "" : parent.getAbsolutePath() + "/" ) + mp3File.getName() + ".cue";

//    path_to_config=Etc.getpwuid.dir+"/.cuefinder"
//    if(!File.exist?(path_to_config))
//      raise "Please provide proxy settings in ~/.cuefinder"
//    end
//    props = load_properties(path_to_config)
//    @proxy_host =props["proxy_host"];
//    @proxy_port =props["proxy_port"];
//    @proxy_set=props["proxy_set"];
//    puts "using proxy: #{@proxy_host}:#{@proxy_port} , set: #{@proxy_set}"

    }

    public abstract String parse_release_no() throws Exception;

    protected int parse_part_no()
    {
        return 0;
    }


    protected String get( String url ) throws IOException
    {
        System.out.println( "downloading url: " + url );
        HttpClient hc = new DefaultHttpClient();
        HttpGet get = new HttpGet( url );
        get.addHeader( "Referer", "http://cuenation.com/?page=cues&folder=gdjb" );
        HttpResponse resp = hc.execute( get );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resp.getEntity().writeTo( baos );
        return new String( baos.toByteArray() );
    }

    public String find_url_to_cue_file( String html ) throws Exception
    {
        String cue_url = parse_url_to_cue_file( html, release_no, part_no );
        if( cue_url == null )
            throw new Exception( "Sorry , can't find url for release #{@release_no}" );

//    cue_url=cue_url[0][0]
//    #urls are returned with &amp; , like a=1&amp;b=2&amp;c=3
//    cue_url=cue_url.gsub("&amp;", "&")
        return cue_url;
    }

    protected abstract String parse_url_to_cue_file( String html, String release_no, int part_no );

    protected String download_cue() throws Exception
    {
        if( release_no != null )
        {
            String html = get( get_radioshow_folder_path() );
            String cue_url = find_url_to_cue_file( html );
            System.out.println( "cueUrl is #{cue_url}, downloading" );
            String content = get( cue_url );
            File f = new File( cue_file_name );
            //todo
//          f.write( content )
//          f.close
            System.out.println( "cue file saved to #{@cue_file_name}" );
//          #puts
//          "#{content}#"
            return cue_file_name;
        } else
        {
            System.out.println( "release no could not be parsed, sorry" );
            return null;
        }
    }

    protected abstract String get_radioshow_folder_path();

    /**
     * #when radioshow is plit into 2 parts,we need to distinguish them by prefix
     * #i.e.:
     * # 101 Tiesto - Track1.mp3
     * # 102 Tiesto - Track2.mp3
     * #... second part begins:
     * # 201 Tiesto - Track1.mp3
     * # 202 Tiesto - Track2.mp3
     */
    String make_output_format()
    {
        String output_format = "@n+@p+@t";
        if( part_no != 0 )
            output_format = "#{@part_no}" + output_format;

        return output_format;
    }

    void call_mp3splt( String cue_file_name, String mp3_file_name ) throws IOException, InterruptedException
    {
        File mp3File = new File( mp3_file_name );
        String dirName = mp3File.getParentFile().getAbsolutePath();
        String output_format = make_output_format();
        String cmd = "mp3splt -o #{output_format} -c \"#{cue_file_name}\" -d \"" + dirName + "\" \"#{mp3_file_name}\"";
        System.out.println( cmd );
        Process proc = Runtime.getRuntime().exec( cmd );
        proc.waitFor();

        if( proc.exitValue() != 0 )
        {
            System.out.println( "could not split mp3 file: #{mp3_file_name}" );
        } else
        {
            if( part_no == 0 )
            {
                mp3File.delete();
            } else
//            System.out.println("mp3 file not deleted (radioshows containing more than 1 part not yet well tested)");
                mp3File.delete();

            new File( cue_file_name ).delete();
        }
    }

    /**
     * convert filenames to UTF-8 , f.e. Tiesto mp3's has specific symbols
     */
    void call_convmv( String mp3_file_name ) throws InterruptedException, IOException
    {
        String dir = new File( mp3_file_name ).getParentFile().getAbsolutePath();
        String cmd = "convmv -f ISO_8859-16 -t UTF-8 \"" + dir + "\"/*.mp3 --notest";
        System.out.println( "converting filenames to UTF-8: #{cmd}" );
        Process proc = Runtime.getRuntime().exec( cmd );
        proc.waitFor();
        if( proc.exitValue() != 0 )
        {
            System.out.println( "could not convert filenames" );
        }
    }

    void process() throws Exception, InterruptedException
    {
        String cue_file = download_cue();
        if( cue_file != null )
        {
            call_mp3splt( cue_file_name, mp3Filename );
            call_convmv( mp3Filename );
        } else
        {
            System.out.println( "could not find cue sheet for: #{@mp3Filename}" );
        }
    }

    String getFirstMatch( String regex, String input )
    {
        Pattern p = Pattern.compile( regex );
        Matcher m = p.matcher( input );
        if( m.find() )
        {
            return m.group( 1 );
        } else
            return null;

    }


}
