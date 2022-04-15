package planning;

import java.util.Optional;

public class SpeechPart extends PartImpl {
	
	private Optional<Text> text = Optional.empty();

	public SpeechPart(String title, String description) {
		super(title, description, PartType.SPEECH);
	}
	
	public void addText(final Text text) {
		this.text = Optional.of(text);
	}
	
	public boolean isTextPresent() {
		return this.text.isPresent();
	}
	
	public Optional<Text> getText() {
		return this.text;
	}

}
