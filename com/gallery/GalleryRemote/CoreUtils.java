package com.gallery.GalleryRemote;

import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.model.Album;

import javax.swing.*;
import java.util.Arrays;
import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: paour
 * Date: Jan 15, 2004
 */
public class CoreUtils {
	static GalleryRemoteCore core = GalleryRemote._().getCore();

	public static void deleteSelectedPictures() {
		JList jPicturesList = core.getPicturesList();
		int[] indices = jPicturesList.getSelectedIndices();
		int selected = jPicturesList.getSelectedIndex();
		Picture reselect = null;

		// find non-selected item after selected index
		Arrays.sort(indices);
		boolean found = false;

		int i = selected + 1;
		while (i < jPicturesList.getModel().getSize()) {
			if (Arrays.binarySearch(indices, i) < 0) {
				found = true;
				break;
			}

			i++;
		}

		if (!found) {
			i = selected - 1;
			while (i >= 0) {
				if (Arrays.binarySearch(indices, i) < 0) {
					found = true;
					break;
				}

				i--;
			}
		}

		if (found) {
			reselect = (Picture) jPicturesList.getModel().getElementAt(i);
		}

		core.getCurrentAlbum().removePictures(indices);

		if (reselect != null) {
			jPicturesList.setSelectedValue(reselect, true);
		}
	}


	/**
	 * Move selected Pictures up
	 */
	public static void movePicturesUp() {
		JList jPicturesList = core.getPicturesList();
		int[] indices = jPicturesList.getSelectedIndices();
		int[] reselect = new int[indices.length];

		Arrays.sort(indices);

		for (int i = 0; i < indices.length; i++) {
			if (indices[i] > 0) {
				Album currentAlbum = core.getCurrentAlbum();

				Picture buf = currentAlbum.getPicture(indices[i]);
				currentAlbum.setPicture(indices[i], currentAlbum.getPicture(indices[i] - 1));
				currentAlbum.setPicture(indices[i] - 1, buf);
				//jPicturesList.setSelectedIndex( indices[i] - 1 );
				reselect[i] = indices[i] - 1;
			} else {
				reselect[i] = indices[i];
			}
		}

		jPicturesList.setSelectedIndices(reselect);
		jPicturesList.ensureIndexIsVisible(jPicturesList.getSelectedIndex());
	}


	/**
	 * Move selected Pictures down
	 */
	public static void movePicturesDown() {
		JList jPicturesList = core.getPicturesList();
		int[] indices = jPicturesList.getSelectedIndices();
		int[] reselect = new int[indices.length];

		Arrays.sort(indices);

		for (int i = indices.length - 1; i >= 0; i--) {
			Album currentAlbum = core.getCurrentAlbum();

			if (indices[i] < currentAlbum.sizePictures() - 1) {
				Picture buf = currentAlbum.getPicture(indices[i]);
				currentAlbum.setPicture(indices[i], currentAlbum.getPicture(indices[i] + 1));
				currentAlbum.setPicture(indices[i] + 1, buf);
				//jPicturesList.setSelectedIndex( sel + 1 );
				reselect[i] = indices[i] + 1;
			} else {
				reselect[i] = indices[i];
			}
		}

		jPicturesList.setSelectedIndices(reselect);
		jPicturesList.ensureIndexIsVisible(jPicturesList.getSelectedIndex());
	}

	public static void selectNextPicture() {
		JList jPicturesList = core.getPicturesList();
		int i = jPicturesList.getSelectedIndex();

		if (i < jPicturesList.getModel().getSize() - 1) {
			jPicturesList.setSelectedIndex(i + 1);
			jPicturesList.ensureIndexIsVisible(i + 1);
		}
	}

	public static void selectPrevPicture() {
		JList jPicturesList = core.getPicturesList();
		int i = jPicturesList.getSelectedIndex();

		if (i > 0) {
			jPicturesList.setSelectedIndex(i - 1);
			jPicturesList.ensureIndexIsVisible(i - 1);
		}
	}

	static class FileCellRenderer extends DefaultListCellRenderer {

		public Component getListCellRendererComponent(
				JList list, Object value, int index,
				boolean selected, boolean hasFocus) {
			super.getListCellRendererComponent(list, value, index, selected, hasFocus);

			if (value != null && index != -1) {
				Picture p = core.getCurrentAlbum().getPicture(index);

				if (p.isOnline()) {
					if (p.getAlbum() != p.getAlbumOnServer()
							|| p.getIndex() != p.getIndexOnServer()) {
						setForeground(Color.RED);
					} else {
						setForeground(Color.GREEN);
					}
				} else {
					setForeground(Color.BLACK);
				}

				if (GalleryRemote._().properties.getShowThumbnails()) {
					ImageIcon icon = core.getThumbnail(p);
					if (icon != null) {
						setIcon(icon);
						setIconTextGap(4 + GalleryRemote._().properties.getThumbnailSize().width - icon.getIconWidth());
					}
				}

				StringBuffer text = new StringBuffer();
				text.append("<html><p>");

				if (p.isOnline()) {
					text.append(p.getName());
				} else {
					File f = p.getSource();
					text.append(f.getName());
					if (GalleryRemote._().properties.getShowPath()) {
						text.append(" [").append(f.getParent()).append("]</p>");
					}
				}

				if (p.getCaption() != null && getIcon() != null) {
					text.append("<p><font color=\"gray\">").append(p.getEscapedCaption()).append("</font></p>");
				}

				text.append("</html>");
				//Log.log(Log.TRACE, MODULE, text.toString());
				setText(text.toString());
			} else {
				setText("dummy");
			}

			return this;
		}
	}}