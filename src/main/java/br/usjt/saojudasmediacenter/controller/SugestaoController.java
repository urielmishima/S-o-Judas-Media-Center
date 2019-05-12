package br.usjt.saojudasmediacenter.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.usjt.saojudasmediacenter.enums.TipoFeedback;
import br.usjt.saojudasmediacenter.model.Conteudo;
import br.usjt.saojudasmediacenter.model.Sugestao;
import br.usjt.saojudasmediacenter.model.Tag;
import br.usjt.saojudasmediacenter.service.ConteudoService;
import br.usjt.saojudasmediacenter.service.SugestaoService;
import br.usjt.saojudasmediacenter.service.TagService;

@Controller
@RequestMapping("/sugestoes")
public class SugestaoController {

	@Autowired private SugestaoService sugestaoService;
	@Autowired private ConteudoService conteudoService;
	@Autowired private TagService tagService;
	
	@PostMapping
	public String sugestao(Sugestao sugestao) {
		sugestaoService.save(sugestao);
		return "redirect:/";
	}	
	

	@Secured("ROLE_ESTAGIARIO")
	@PostMapping("/positivo/{id}")
	public String sugestaoAceitar(@PathVariable String id) {
		Sugestao sugestao = sugestaoService.findById(id);
		sugestao.setFeedback(TipoFeedback.POSITIVO);
		Tag tag = tagService.cadastrar(sugestao.getSugestao());
		conteudoService.cadastrarTag(
				new Conteudo().setTags(Arrays.asList(tag))
							  .setId(sugestao.getConteudo().getId())
				);
		sugestaoService.save(sugestao);
		return "redirect:/";
	}
	

	@Secured("ROLE_ESTAGIARIO")
	@PostMapping("/negativo/{id}")
	public String sugestaoRecusar(@PathVariable String id) {
		Sugestao sugestao = sugestaoService.findById(id);
		sugestao.setFeedback(TipoFeedback.NEGATIVO);
		sugestaoService.save(sugestao);
		return "redirect:/";
	}
	
	@GetMapping("/dashboard")
	@Secured("ROLE_ESTAGIARIO")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView("home");
		mav.addObject("sugestoes", sugestaoService.findByFeedbackIsNull());
		return mav;
	}
}