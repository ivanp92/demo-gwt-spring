package org.planificando.server.service.impl;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.planificando.server.dao.UserMapper;
import org.planificando.server.model.User;
import org.planificando.server.service.UserService;
import org.planificando.server.utils.JsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserMapper userMapper;

	private JsonBuilder jsonBuilder = new JsonBuilder();

	@SuppressWarnings({ "unchecked" })
	@Override
	@Transactional
	public String saveUsers(String json)
	{
		String respuesta = "";

		try
		{
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);

			User user = new User();

			if (jsonObject.get("codUser") != null)
				user.setCodUser(Long.parseLong(jsonObject.get("codUser").toString()));

			if (jsonObject.get("nick") != null)
				user.setNick(jsonObject.get("nick").toString());

			if (jsonObject.get("pass") != null)
				user.setPass(jsonObject.get("pass").toString());

			if (jsonObject.get("email") != null)
				user.setEmail(jsonObject.get("email").toString());

			user.setRegistered(new Date());
			user.setBanned(false);

			if (user.getCodUser() == null)
				userMapper.insert(user);
			else
			{
				userMapper.update(user);

				user = userMapper.selectByCodUser(user.getCodUser());
			}

			JSONObject response = new JSONObject();

			jsonObject.put("codUser", user.getCodUser());

			JSONArray jsonArray = new JSONArray();

			jsonArray.add(jsonObject);

			response.put("totalRows", 1);
			response.put("endRow", 1);
			response.put("startRow", 0);
			response.put("status", 0);
			response.put("data", jsonArray);

			jsonBuilder.generateResponse(response);

			respuesta = jsonBuilder.getRespuesta();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return respuesta;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public String fetchUsers(String json)
	{
		String respuesta = "";

		try
		{
			List<User> usuarios = userMapper.selectAll();

			JSONObject respuestaJSON = new JSONObject();

			JSONObject response = new JSONObject();

			JSONArray jsonArray = new JSONArray();

			for (User user : usuarios)
				jsonArray.add(jsonBuilder.generateJson(user));

			response.put("totalRows", usuarios.size());
			response.put("endRow", usuarios.size());
			response.put("startRow", 0);
			response.put("status", 0);
			response.put("data", jsonArray);

			respuestaJSON.put("response", response);

			respuesta = respuestaJSON.toString();

		}
		catch (Exception ex)

		{
			ex.printStackTrace();
		}

		return respuesta;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public String removeUsers(String json)
	{
		String respuesta = "";

		JSONObject jsonObject;
		
		try
		{
			jsonObject = (JSONObject) new JSONParser().parse(json);

			Long codUser = Long.parseLong(jsonObject.get("codUser").toString());

			userMapper.deleteByPrimaryKey(codUser);
			
			JSONObject jsoObject = new JSONObject();
			
			jsonObject.put("status", 0);
			
			jsonBuilder.generateResponse(jsoObject);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return respuesta;
	}
}
