#!/usr/bin/ruby
require "uri"
require "net/http"
require "cflib"

file_name=ARGV[0]
#file_name="Armin van Buuren presents - A State of Trance Episode 430.mp3##"
#file_name="Markus Schulz - Global DJ Broadcast (2010-03-11) [PS].mp3"

def normalize_file_name(file_name)
return file_name.gsub(" ", "\\ ").gsub("(","\\(").gsub(")","\\)")
end


if(file_name != nil) then
#  ap=ASOTParser.new(file_name )
  ap=MarkusShultzParser.new(file_name )
  cue_file=ap.download_cue()
  if cue_file!=nil then
    ret=system("mp3splt -c #{cue_file} #{normalize_file_name(file_name)}")
    if (!ret) then
      puts "could not split mp3 file"
    end
  else
    puts "could not find cue sheet for: #{file_name }"
  end
else
  puts "Please provide file name"
end
