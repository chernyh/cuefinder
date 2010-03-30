#!/usr/bin/ruby
require "cflib"
require "test/unit"
require 'etc'

class CueFinderTest < Test::Unit::TestCase


  def test_ParseGDJBRelease_pattern1
    file_name="Markus Schulz - Global DJ Broadcast (2010-03-11) [PS].mp3"
  
  end


  def test_ParseGDJB
    html = <<ENDOF_URL
    <a href="download.php?type=cue&folder=gdjb&filename=01-markus_schulz_-_global_dj_broadcast_%28di.fm%29_11-03-2010-tt.cue"><img src="layout/download.png" alt="Download!" /></a> <a href="?page=tracklist&folder=gdjb&filename=01-markus_schulz_-_global_dj_broadcast_%28di.fm%29_11-03-2010-tt.cue"> Global DJ Broadcast (2010-03-11) [TT]</a>
ENDOF_URL
    releaseNo = "2010-03-11"
    year,month,date = releaseNo.scan(/\d+/)
    cue_url_release_pattern="#{date}-#{month}-#{year}"
    puts "cue_url_release_pattern=#{cue_url_release_pattern}"
    urls=html.scan /(download.php[?]type=cue.*_#{cue_url_release_pattern}.*\.cue)\"\>\<img/
    puts "scanned urls array: #{urls}"
    url=urls[0][0].gsub("&", "&")
    puts "scanned url: #{url}"
  end

  def test_ParseGDJBRelease2
    file_name = "Markus Schulz presents - Global DJ Broadcast WMC Special (25 March 2010).mp3"
    cf=MarkusShultzParser.new( file_name )
    rel_no=cf.parse_release_no()
    puts "releaseNo='#{rel_no}'"

  end

  def test_CueUrlFromHtmlPage
    html= <<ENDOF_URL
<a href="download.php?type=cue&amp;folder=gdjb&amp;filename=01-markus_schulz_-_global_dj_broadcast_wmc_special-%28di.fm%29-net-25-03-2010-mjm.cue"><img src="layout/download.png" alt="Download!" /></a> <a href="?page=tracklist&amp;folder=gdjb&amp;filename=01-markus_schulz_-_global_dj_broadcast_wmc_special-%28di.fm%29-net-25-03-2010-mjm.cue"> Global DJ Broadcast (2010-03-25) (WMC 2010 Edition) [PS/MJM]</a><br /> 
<a href="download.php?type=cue&amp;folder=gdjb&amp;filename=01-Markus_Schulz_-_Global_DJ_Broadcast-NET-2010-03-25-iRUSH.cue"><img src="layout/download.png" alt="Download!" /></a> <a href="?page=tracklist&amp;folder=gdjb&amp;filename=01-Markus_Schulz_-_Global_DJ_Broadcast-NET-2010-03-25-iRUSH.cue"> Global DJ Broadcast (2010-03-25) (WMC 2010 Edition) [iRUSH]</a><br />
ENDOF_URL

    cf=MarkusShultzParser.new( "" )
    cf.parse_url_to_cue_file(html,"25-03-2010")

  end

  def test_CueFinderFactory
    file_name = "Markus Schulz presents - Global DJ Broadcast WMC Special (25 March 2010).mp3"
    assert_instance_of(MarkusShultzParser, CueFinderFactory.make_cuefinder(file_name))

    file_name = "Armin van Buuren presents - A State of Trance Episode 430.mp3"
    assert_instance_of(ASOTParser, CueFinderFactory.make_cuefinder(file_name))

    file_name = "DJ_Shah_-_Magic_Island_-_Music_for_Balearic_People_Episode_004-NET-23-05-2008-iRUSH.mp3"
    assert_instance_of(MagicIslandParser, CueFinderFactory.make_cuefinder(file_name))
  end

  def test_MagicIsland_parse_release_no
    file_name = "DJ_Shah_-_Magic_Island_-_Music_for_Balearic_People_Episode_004-NET-23-05-2008-iRUSH"
    cf=MagicIslandParser.new( file_name )
    rel_no=cf.parse_release_no( )
    assert_equal("004", rel_no)
  end
  def test_MagicIsland_parse_cue_url

    cf=MagicIslandParser.new( "" )
    html =<<ENDOF_URL
    <a href="download.php?type=cue&amp;folder=magicisland&amp;filename=01-roger_shah_magic_island_-_music_for_balearic_people_098_%28di.fm%29_26-03-2010-tt.cue"><img src="layout/download.png" alt="Download!" /></a> <a href="?page=tracklist&amp;folder=magicisland&amp;filename=01-roger_shah_magic_island_-_music_for_balearic_people_098_%28di.fm%29_26-03-2010-tt.cue"> Magic Island - Music for Balearic People 098 (2010-03-26) [TT]</a><br />
ENDOF_URL

    url=cf.parse_url_to_cue_file(html,"098")
    
    assert_equal("download.php?type=cue&folder=magicisland&filename=01-roger_shah_magic_island_-_music_for_balearic_people_098_%28di.fm%29_26-03-2010-tt.cue", url)
  end

end