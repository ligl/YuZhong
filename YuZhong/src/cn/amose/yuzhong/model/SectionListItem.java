package cn.amose.yuzhong.model;

import java.text.Collator;

public class SectionListItem<T> implements Comparable<SectionListItem<T>> {
	public T item;
	public String section;

	public SectionListItem(T item, String section) {
		this.item = item;
		this.section = section;
	}

	@Override
	public String toString() {
		return item.toString();
	}

	@Override
	public int compareTo(SectionListItem<T> another) {
		int category = this.section.codePointAt(0)
				- another.section.codePointAt(0);
		if (category != 0) {
			return category;
		}
		String o1 = this.item.toString();
		String o2 = another.item.toString();
		Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);

		if (myCollator.compare(o1, o2) < 0) {
			return -1;
		} else if (myCollator.compare(o1, o2) > 0) {
			return 1;
		} else {
			return 0;
		}
	}
}
