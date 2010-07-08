// $Id$
package ru.cuefinder;

import java.util.ArrayList;
import java.util.List;

public class Logger
{
    private List<String> log = new ArrayList<String>();

    public void add( String msg )
    {
        log.add( msg );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for( String s : log )
        {
            sb.append( s ).append( "\n" );
        }
        return sb.toString();
    }
}
