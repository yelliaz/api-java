package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class MessageJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Message, JsonElement> transformer;
	
	private Message message;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new MessageJsonTransformer(crm);
		message = new Message(new Identifier("abc"), "error", "prop", 
			new Localized("message.reason", "English reason", "Raison française"));
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Message.class, transformer.getSourceType());
	}

	@Test
	public void testFormatNull() throws Exception {
		assertNull(transformer.format(null, null));
		assertNull(transformer.format(null, Lang.ROOT));
		assertNull(transformer.format(null, Lang.ENGLISH));
		assertNull(transformer.format(null, Lang.FRENCH));
	}

	@Test
	public void testLinkedJson() throws Exception {
		JsonObject linked = (JsonObject)transformer.format(message, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@type", "identifier", "type", "path", "reason"), linked.keys());
		assertEquals("Message", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("identifier").keys());
		assertEquals("Identifier", linked.getObject("identifier").getString("@type"));
		assertEquals("abc", linked.getObject("identifier").getString("@id"));
		assertEquals("error", linked.getString("type"));
		assertEquals("prop", linked.getString("path"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("reason").keys());
		assertEquals("Localized", linked.getObject("reason").getString("@type"));
		assertEquals("message.reason", linked.getObject("reason").getString("@value"));
		assertEquals("English reason", linked.getObject("reason").getString("@en"));
		assertEquals("Raison française", linked.getObject("reason").getString("@fr"));
		assertEquals(message, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(message, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("@type", "identifier", "type", "path", "reason"), root.keys());
		assertEquals("Message", root.getString("@type"));
		assertEquals("abc", root.getString("identifier"));
		assertEquals("error", root.getString("type"));
		assertEquals("prop", root.getString("path"));
		assertEquals("message.reason", root.getString("reason"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(message, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "identifier", "type", "path", "reason"), english.keys());
		assertEquals("Message", english.getString("@type"));
		assertEquals("abc", english.getString("identifier"));
		assertEquals("error", english.getString("type"));
		assertEquals("prop", english.getString("path"));
		assertEquals("English reason", english.getString("reason"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(message, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "identifier", "type", "path", "reason"), french.keys());
		assertEquals("Message", french.getString("@type"));
		assertEquals("abc", french.getString("identifier"));
		assertEquals("error", french.getString("type"));
		assertEquals("prop", french.getString("path"));
		assertEquals("Raison française", french.getString("reason"));
	}
	
}