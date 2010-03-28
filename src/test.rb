#!/usr/bin/ruby
require "cflib"

def testParseGDJBRelease
  file_name="Markus Schulz - Global DJ Broadcast (2010-03-11) [PS].mp3"
  release_no=file_name.scan /Global DJ Broadcast \((.*)\).*\.mp3/
  puts "release_no=#{release_no[0][0]}"
  year,month,date = release_no.scan(/\d+/)
  cue_url_release_pattern="#{date}-#{month}-#{year}"
end


def testParseGDJB
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

def testParseGDJBRelease2
file_name = "Markus Schulz presents - Global DJ Broadcast WMC Special (25 March 2010).mp3"
cf=MarkusShultzParser.new( file_name )
rel_no=cf.parse_release_no()
puts "releaseNo='#{rel_no}'"

end

def testCueUrlFromHtmlPage
  html= <<ENDOF_URL
<a href="download.php?type=cue&amp;folder=gdjb&amp;filename=01-markus_schulz_-_global_dj_broadcast_wmc_special-%28di.fm%29-net-25-03-2010-mjm.cue"><img src="layout/download.png" alt="Download!" /></a> <a href="?page=tracklist&amp;folder=gdjb&amp;filename=01-markus_schulz_-_global_dj_broadcast_wmc_special-%28di.fm%29-net-25-03-2010-mjm.cue"> Global DJ Broadcast (2010-03-25) (WMC 2010 Edition) [PS/MJM]</a><br /> 
<a href="download.php?type=cue&amp;folder=gdjb&amp;filename=01-Markus_Schulz_-_Global_DJ_Broadcast-NET-2010-03-25-iRUSH.cue"><img src="layout/download.png" alt="Download!" /></a> <a href="?page=tracklist&amp;folder=gdjb&amp;filename=01-Markus_Schulz_-_Global_DJ_Broadcast-NET-2010-03-25-iRUSH.cue"> Global DJ Broadcast (2010-03-25) (WMC 2010 Edition) [iRUSH]</a><br />
ENDOF_URL

  cf=MarkusShultzParser.new( "" )
cf.parse_url_to_cue_file(html,"25-03-2010")

end

def testFileNamesWithSpaces
  cue_file_name=ARGV[0]
  mp3_file_name=ARGV[1]
  call_mp3splt(cue_file_name,mp3_file_name)
end



testFileNamesWithSpaces