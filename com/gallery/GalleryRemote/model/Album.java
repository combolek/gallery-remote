/*
 *  Gallery Remote - a File Upload Utility for Gallery
 *
 *  Gallery - a web based photo album viewer and editor
 *  Copyright (C) 2000-2001 Bharat Mediratta
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or (at
 *  your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.gallery.GalleryRemote.model;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import com.gallery.GalleryRemote.*;

/**
 *  Album model
 *
 *@author     paour
 *@created    11 ao�t 2002
 */

public class Album extends Picture implements ListModel
{
	public static final String MODULE="Album";
	
	Vector pictures = new Vector();
	String name = "Not yet connected to Gallery";
	String url;
	String username;
	String password;
	long pictureFileSize = -1;
	Gallery gallery = null;

	// ListModel
	Vector listeners = new Vector( 1 );

	/**
	 *  Sets the gallery attribute of the Album object
	 *
	 *@param  gallery  The new gallery
	 */
	public void setGallery( Gallery gallery ) {
		this.gallery = gallery;
	}


	/**
	 *  Gets the gallery attribute of the Album object
	 *
	 *@return    The gallery
	 */
	public Gallery getGallery() {
		return gallery;
	}


	/**
	 *  Gets the pictures inside the album
	 *
	 *@return    The pictures value
	 */
	public Enumeration getPictures() {
		return pictures.elements();
	}


	/**
	 *  Adds a picture to the album
	 *
	 *@param  p  the picture to add. This will change its parent album
	 */
	public void addPicture( Picture p ) {
		p.setAlbum( this );
		pictures.addElement( p );

		notifyListeners();
	}


	/**
	 *  Adds a picture to the album
	 *
	 *@param  file  the file to create the picture from
	 */
	public void addPicture( File file ) {
		Picture p = new Picture( file );
		p.setAlbum( this );
		pictures.addElement( p );

		notifyListeners();
	}


/**
	 *  Adds pictures to the album
	 *
	 *@param  files  the files to create the pictures from
	 */
	public void addPictures( File[] files ) {
		this.addPictures(files, 0);
	}
        
        
        /**
	 *  Adds pictures to the album at a specified index
	 *
	 *@param  files  the files to create the pictures from
         *@param  index  the index in the list at which to begin adding
	 */
	public void addPictures( File[] files, int index ) {
		for ( int i = 0; i < files.length; i++ ) {
			Picture p = new Picture( files[i] );
			p.setAlbum( this );
			pictures.add( index++, p );
		}

		notifyListeners();
	}


	/**
	 *  Number of pictures in the album
	 *
	 *@return    Number of pictures in the album
	 */
	public int sizePictures() {
		return pictures.size();
	}


	/**
	 *  Remove all the pictures
	 */
	public void clearPictures() {
		pictures.clear();

		notifyListeners();
	}


	/**
	 *  Remove a picture
	 *
	 *@param  n  item number of the picture to remove
	 */
	public void removePicture( int n ) {
		pictures.remove( n );

		ListDataEvent lde = new ListDataEvent( com.gallery.GalleryRemote.GalleryRemote.getInstance().mainFrame, ListDataEvent.INTERVAL_REMOVED, n, n );
		notifyListeners(lde);
	}


	/**
	 *  Remove pictures
	 *
	 *@param  indices  list of indices of pictures to remove
	 */
	public void removePictures( int[] indices ) {
		int min, max;
		min = max = indices[0];
		
		for ( int i = indices.length - 1; i >= 0; i-- ) {
			pictures.remove( indices[i] );
			if (indices[i] > max) max = indices[i];
			if (indices[i] < min) min = indices[i];
		}

		ListDataEvent lde = new ListDataEvent( com.gallery.GalleryRemote.GalleryRemote.getInstance().mainFrame, ListDataEvent.INTERVAL_REMOVED, min, max );
		notifyListeners(lde);
	}


	/**
	 *  Get a picture from the album
	 *
	 *@param  n  index of the picture to retrieve
	 *@return    The Picture
	 */
	public Picture getPicture( int n ) {
		return (Picture) pictures.get( n );
	}


	/**
	 *  Set a picture in the album
	 *
	 *@param  n  index of the picture
	 *@param  p  The new picture
	 */
	public void setPicture( int n, Picture p ) {
		pictures.set( n, p );

		notifyListeners();
	}


	/**
	 *  Get the list of files that contain the pictures
	 *
	 *@return    The fileList value
	 */
	public ArrayList getFileList() {
		ArrayList l = new ArrayList( pictures.size() );

		Enumeration e = pictures.elements();
		while ( e.hasMoreElements() ) {
			l.add( ( (Picture) e.nextElement() ).getSource() );
		}

		return l;
	}


	/**
	 *  Sets the name attribute of the Album object
	 *
	 *@param  name  The new name value
	 */
	public void setName( String name ) {
		this.name = name;
	}


	/**
	 *  Gets the name attribute of the Album object
	 *
	 *@return    The name value
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Gets the aggregated file size of all the pictures in the album
	 *
	 *@return    The file size (bytes)
	 */
	public long getPictureFileSize() {
		if ( pictureFileSize == -1 ) {
			pictureFileSize = getPictureFileSize( (Picture[]) pictures.toArray( new Picture[0] ) );
		}

		return pictureFileSize;
	}


	/**
	 *  Gets the aggregated file size of a list of pictures
	 *
	 *@param  pictures  the list of Pictures
	 *@return           The file size (bytes)
	 */
	public static long getPictureFileSize( Picture[] pictures ) {
		return getObjectFileSize( pictures );
	}


	/**
	 *  Gets the aggregated file size of a list of pictures Unsafe, the Objects
	 *  will be cast to Pictures.
	 *
	 *@param  pictures  the list of Pictures
	 *@return           The file size (bytes)
	 */
	public static long getObjectFileSize( Object[] pictures ) {
		long total = 0;

		for ( int i = 0; i < pictures.length; i++ ) {
			total += ( (Picture) pictures[i] ).getFileSize();
		}

		return total;
	}


	/*
	 *	ListModel Implementation
	 */
	/**
	 *  Gets the size attribute of the Album object
	 *
	 *@return    The size value
	 */
	public int getSize() {
		return pictures.size();
	}


	/**
	 *  Gets the elementAt attribute of the Album object
	 *
	 *@param  index  Description of Parameter
	 *@return        The elementAt value
	 */
	public Object getElementAt( int index ) {
		return pictures.elementAt( index );
	}


	/**
	 *  Adds a feature to the ListDataListener attribute of the Album object
	 *
	 *@param  ldl  The feature to be added to the ListDataListener attribute
	 */
	public void addListDataListener( ListDataListener ldl ) {
		listeners.addElement( ldl );
	}


	/**
	 *  Description of the Method
	 *
	 *@param  ldl  Description of Parameter
	 */
	public void removeListDataListener( ListDataListener ldl ) {
		listeners.removeElement( ldl );
	}


	void notifyListeners() {
		ListDataEvent lde = new ListDataEvent( com.gallery.GalleryRemote.GalleryRemote.getInstance().mainFrame, ListDataEvent.CONTENTS_CHANGED, 0, pictures.size() );
		
		notifyListeners(lde);
	}
	
	void notifyListeners(ListDataEvent lde) {
		pictureFileSize = -1;
		
		Log.log(Log.TRACE, MODULE, "Firing ListDataEvent=" + lde.toString());
		Enumeration e = listeners.elements();
		while ( e.hasMoreElements() ) {
			ListDataListener ldl = (ListDataListener) e.nextElement();
			ldl.contentsChanged( lde );
		}
	}
}

