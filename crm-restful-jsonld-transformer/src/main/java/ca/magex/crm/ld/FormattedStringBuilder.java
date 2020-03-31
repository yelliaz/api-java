package ca.magex.crm.ld;

public class FormattedStringBuilder {

	private StringBuilder sb;
	
	private int indent;
	
	public FormattedStringBuilder() {
		this.sb = new StringBuilder();
		this.indent = 0;
	}
	
	public FormattedStringBuilder append(String text) {
		for (int i = 0; i < indent; i++) {
			sb.append("\t");
		}
		sb.append(text);
		sb.append("\n");
		return this;
	}
	
	public FormattedStringBuilder indent(String text) {
		append(text);
		indent += 1;
		return this;
	}
	
	public FormattedStringBuilder unindent(String text) {
		indent -= 1;
		append(text);
		return this;
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
	
}
