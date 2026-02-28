mig33.Chatroom = function(
					name,
					description,
					max_size,
					size,
					adult_only,
					category,
					id,
					user_owned,
					group_id)
{
	this.id = id;
	this.name = name;
	this.description = description;
	this.max_size = max_size;
	this.size = size;
	this.adult_only = adult_only;
	this.category = category;
	this.user_owned = user_owned;
	this.group_id = group_id;
}