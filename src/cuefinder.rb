#!/usr/bin/ruby
require "uri"
require "net/http"
require "cflib"

file_name=ARGV[0]
#file_name="Armin van Buuren presents - A State of Trance Episode 430.mp3##"
#file_name="Markus Schulz - Global DJ Broadcast (2010-03-11) [PS].mp3"
#cmd_line="mp3splt -c cue.cue \"#{file_name}\"#"
#puts cmd_line
#system(cmd_line)

if(file_name != nil) then
  #  ap=ASOTParser.new(file_name )
  ap=MarkusShultzParser.new(file_name )
  cue_file=ap.download_cue()
  if cue_file!=nil then
    call_mp3splt(ap.cue_file_name,ap.mp3_file_name)
  else
    puts "could not find cue sheet for: #{file_name}"
  end
else
  puts "Please provide file name"
end
