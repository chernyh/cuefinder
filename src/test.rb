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

testParseGDJB
#testParseGDJBRelease