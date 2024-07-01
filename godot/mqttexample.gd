extends Control

var receivedmessagecount = 0
var waiting = false
var ui_hidden = false
var zero_sent = false
@onready var jug = $Node3D/CharacterBody3D
@onready var water = $Node3D/CharacterBody3D/GPUParticles3D

func _ready():
	connectedactionsactive(false)
	
func _process(_delta):
	if Input.is_action_just_pressed("ui_accept"):
		if ui_hidden:
			self.show()
			ui_hidden = false
		else:
			self.hide()
			ui_hidden = true
			
	if jug.rotation_degrees.x >= 40.0 and not waiting:
		zero_sent = false
		water.emitting = true
		waiting = true
		$MQTT.publish($VBox/HBoxPublish/publishtopic.text, 
				str(randf_range(0.1, 0.6)), 
				$VBox/HBoxPublish/publishretain.button_pressed, 
				0)
		await get_tree().create_timer(0.5).timeout
		waiting = false
	if jug.rotation_degrees.x < 40.0:
		water.emitting = false
		if not zero_sent:
			$MQTT.publish($VBox/HBoxPublish/publishtopic.text, 
					"0.0", 
					$VBox/HBoxPublish/publishretain.button_pressed, 
					0)
			zero_sent = true

func _on_brokerprotocol_item_selected(index):
	# port defaults for [tcp, ssl, ws, wss] used at https://test.mosquitto.org/
	$VBox/HBoxBroker/brokerport.text = "%d" % ([ 1883, 8886, 8080, 8081 ][index])

func _on_button_connect_toggled(button_pressed):
	if button_pressed:
		randomize()
		$MQTT.client_id = "s%d" % randi()

		if $VBox/HBoxLastwill/lastwilltopic.text:
			$MQTT.set_last_will($VBox/HBoxLastwill/lastwilltopic.text, 
								$VBox/HBoxLastwill/lastwillmessage.text, 
								$VBox/HBoxLastwill/lastwillretain.button_pressed)
		else:
			$MQTT.set_last_will("", "", false)
			
		$VBox/HBoxBrokerControl/status.text = "connecting..."
		var brokerurl = $VBox/HBoxBroker/brokeraddress.text
		var protocol = $VBox/HBoxBroker/brokerprotocol.get_item_text($VBox/HBoxBroker/brokerprotocol.selected)
		$MQTT.connect_to_broker("%s%s:%s" % [protocol, brokerurl, $VBox/HBoxBroker/brokerport.text])

	else:
		$VBox/HBoxBrokerControl/status.text = "disconnecting..."
		$MQTT.disconnect_from_server()

func brokersettingsactive(active):
	$VBox/HBoxBroker/brokeraddress.editable = active
	$VBox/HBoxBroker/brokerport.editable = active
	$VBox/HBoxBroker/brokerprotocol.disabled = not active
	$VBox/HBoxLastwill/lastwilltopic.editable = active
	$VBox/HBoxLastwill/lastwillmessage.editable = active
	$VBox/HBoxLastwill/lastwillretain.disabled = not active
	$VBox/HBoxBrokerControl/ButtonConnect.button_pressed = not active

func connectedactionsactive(active):
	$VBox/HBoxSubscriptions/subscribetopic.editable = active
	$VBox/HBoxSubscriptions/subscribe.disabled = not active
	$VBox/HBoxPublish/publishtopic.editable = active
	$VBox/HBoxPublish/publishmessage.editable = active
	$VBox/HBoxPublish/publishretain.disabled = not active
	$VBox/HBoxPublish/publish.disabled = not active
	if not active:
		$VBox/HBoxSubscriptions/subscriptions.clear()
	$VBox/HBoxSubscriptions/subscriptions.disabled = true
	$VBox/HBoxSubscriptions/unsubscribe.disabled = true

func _on_mqtt_broker_connected():
	$VBox/HBoxBrokerControl/status.text = "connected."
	brokersettingsactive(false)
	connectedactionsactive(true)
	receivedmessagecount = 0
	
func _on_mqtt_broker_disconnected():
	$VBox/HBoxBrokerControl/status.text = "disconnected."
	brokersettingsactive(true)
	connectedactionsactive(false)

func _on_mqtt_broker_connection_failed():
	$VBox/HBoxBrokerControl/status.text = "failed."
	brokersettingsactive(true)
	connectedactionsactive(false)


func _on_mqtt_received_message(topic, message):
	if receivedmessagecount == 0:
		$VBox/subscribedmessages.clear()
	receivedmessagecount += 1
	$VBox/subscribedmessages.append_text("[b]%s[/b] %s\n" % [topic, message])

func _on_publish_pressed():
	var qos = 0
	$MQTT.publish($VBox/HBoxPublish/publishtopic.text, 
				$VBox/HBoxPublish/publishmessage.text, 
				$VBox/HBoxPublish/publishretain.button_pressed, 
				qos)

func _on_subscribe_pressed():
	var qos = 0
	var topic = $VBox/HBoxSubscriptions/subscribetopic.text.strip_edges()
	$MQTT.subscribe(topic, qos)
	for i in range($VBox/HBoxSubscriptions/subscriptions.item_count):
		if topic == $VBox/HBoxSubscriptions/subscriptions.get_item_text(i):
			return
	$VBox/HBoxSubscriptions/subscriptions.add_item(topic)
	$VBox/HBoxSubscriptions/subscriptions.select($VBox/HBoxSubscriptions/subscriptions.item_count - 1)
	$VBox/HBoxSubscriptions/subscriptions.disabled = false
	$VBox/HBoxSubscriptions/unsubscribe.disabled = false

func _on_unsubscribe_pressed():
	var seloptbutt = $VBox/HBoxSubscriptions/subscriptions
	var sel = seloptbutt.selected
	var topic = seloptbutt.get_item_text(sel)
	$MQTT.unsubscribe(topic)
	seloptbutt.remove_item(seloptbutt.selected)
	$VBox/HBoxSubscriptions/subscriptions.disabled = (seloptbutt.item_count == 0)
	$VBox/HBoxSubscriptions/unsubscribe.disabled = (seloptbutt.item_count == 0)
	if seloptbutt.item_count != 0:
		seloptbutt.select(min(sel, seloptbutt.item_count - 1))


