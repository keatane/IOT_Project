extends CharacterBody3D

func _ready():
	pass
	
func _process(_delta):
	if Input.is_action_pressed("ui_right") or Input.is_action_pressed("ui_up") :
		rotation_degrees.x = lerp(rotation_degrees.x, -15.0, 0.1)
	if Input.is_action_pressed("ui_left") or Input.is_action_pressed("ui_down") :
		rotation_degrees.x = lerp(rotation_degrees.x, 45.0, 0.1)
