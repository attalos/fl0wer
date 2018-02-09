package org.attalos.owlTest.blocking;

import org.attalos.owlTest.subsumption.ApplicableRule;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by attalos on 7/2/17.
 */
public abstract class BlockingElement {

    /**
     *
     * @return all elem_ids which are in this blocking condition or null if there is only one elem
     */
    protected abstract SortedSet<Long> get_blocking_elements();
    ///**
     //*
     //* @param elem_id elem to remove
     //* @return list of previously block rules which aren't block anymore and should get put back in the queue.
     //* If there was only one element and nothing blocked, the returnvalue is null.
     //* If there was an blocked element but no rule gets released the returnvalue is an empty list.
     //*/

    ///**
     //*
     //* @param elem_id
     //* @return null, if there is no element left - the new Blocking Element otherwise
     //*/
    /*protected abstract BlockingElement remove_elem(Long elem_id);

    /**
     *
     * @param elem_id elem to insert
     * @return the new Blocking Element
     *
    protected abstract BlockingElement insert_elem(Long elem_id);*/
}
