package sk.is.urso.service;

import org.alfa.model.common.IUser;
import org.alfa.service.IUserService;
import org.alfa.utils.TypeResolverUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.is.urso.model.InternyPouzivatel;
import sk.is.urso.repository.InternyPouzivatelRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private InternyPouzivatelRepository internyPouzivatelRepository;

    @Override
    public IUser getCurrentUser() {
        Optional<InternyPouzivatel> internalUserOptional = internyPouzivatelRepository.findFirstByOrderByIdAsc();
        if (internalUserOptional.isPresent()) {
            return internalUserOptional.get();
        } else {
            return null;
        }
    }

    @Override
    public IUser getCurrentUser(Class entityClazz) { // TODO: toto sa bude menit
        Map<String, Class<?>> resolvedTypes = TypeResolverUtils.resolveTypesIfc(entityClazz);
        Class<? extends IUser> tClass = (Class<? extends IUser>) resolvedTypes.get("T");

        if (tClass == null) {
            return null;
        }

        if (InternyPouzivatel.class.equals(tClass)) {
            Optional<InternyPouzivatel> internalUserOptional = internyPouzivatelRepository.findFirstByOrderByIdAsc();
            if (!internalUserOptional.isPresent()) {
                return null;
            }
            return internalUserOptional.get();
        }

        return null;
    }
}
