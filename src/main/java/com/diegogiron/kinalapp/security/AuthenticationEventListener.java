package com.diegogiron.kinalapp.security;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UsuarioRepository usuarioRepository;

    public AuthenticationEventListener(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario != null) {
            try {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attrs.getRequest().getSession();
                session.setAttribute("usuario", usuario);
            } catch (IllegalStateException ignored) {
            }
        }
    }
}